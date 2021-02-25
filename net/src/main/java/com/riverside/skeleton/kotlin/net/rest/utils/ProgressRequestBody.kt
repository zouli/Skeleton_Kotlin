package com.riverside.skeleton.kotlin.net.rest.utils

import android.os.Handler
import android.os.Looper
import com.riverside.skeleton.kotlin.util.resource.uuid
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Companion.MIXED
import okhttp3.RequestBody
import okio.BufferedSink
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody(
    val boundary: ByteString, val type: MediaType, val parts: List<Part>
) : RequestBody() {
    private lateinit var callbacks: UploadCallbacks
    private val contextType =
        (type.toString() + "; boundary=" + boundary.utf8()).toMediaTypeOrNull()

    private val COLON_SPACE = byteArrayOf(':'.toByte(), ' '.toByte())
    private val CRLF = byteArrayOf('\r'.toByte(), '\n'.toByte())
    private val DASH_DASH = byteArrayOf('-'.toByte(), '-'.toByte())
    private val DEFAULT_BUFFER_SIZE = 2048

    override fun contentType(): MediaType? = contextType

    override fun writeTo(sink: BufferedSink) {
        (parts.indices).map { p ->
            val part = parts[p]
            val headers = part.headers
            callbacks = part.callbacks
            val url = part.url

            val file = File(url)
            val fileLength = file.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val fis = FileInputStream(file)
            var uploaded: Long = 0

            sink.write(DASH_DASH)
            sink.write(boundary)
            sink.write(CRLF)

            (0 until headers.size).map { h ->
                sink.writeUtf8(headers.name(h))
                    .write(COLON_SPACE)
                    .writeUtf8(headers.value(h))
                    .write(CRLF)
            }

            val contentType = contentType()
            if (contentType != null) {
                sink.writeUtf8("Content-Type: ")
                    .writeUtf8(contentType.toString())
                    .write(CRLF)
            }

            if (fileLength != -1L) {
                sink.writeUtf8("Content-Length: ")
                    .writeDecimalLong(fileLength)
                    .write(CRLF)
            }

            sink.write(CRLF)
            val handler = Handler(Looper.getMainLooper())
            try {
                var read: Int
                do {
                    read = fis.read(buffer)
                    if (read == -1) break

                    // update progress on UI thread
                    handler.post(ProgressUpdater(url, uploaded, fileLength))

                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                } while (true)
            } finally {
                fis.close()
                handler.post(ProgressUpdater(url, fileLength, fileLength))
            }

            sink.write(CRLF)
        }

        sink.write(DASH_DASH)
        sink.write(boundary)
        sink.write(DASH_DASH)
        sink.write(CRLF)
    }

    class Part private constructor(
        val headers: Headers,
        val url: String,
        val callbacks: UploadCallbacks
    ) {
        companion object {
            private fun create(
                headers: Headers,
                url: String,
                callbacks: UploadCallbacks
            ): Part {
                require(headers["Content-Type"] == null) { "Unexpected header: Content-Type" }
                require(headers["Content-Length"] == null) { "Unexpected header: Content-Length" }
                return Part(headers, url, callbacks)
            }

            fun createFormData(
                name: String, filename: String?, url: String,
                callbacks: UploadCallbacks
            ): Part {
                val disposition = StringBuilder("form-data; name=")
                appendQuotedString(disposition, name)

                if (filename != null) {
                    disposition.append("; filename=")
                    appendQuotedString(disposition, filename)
                }

                return create(
                    headersOf("Content-Disposition", disposition.toString()),
                    url, callbacks
                )
            }

            private fun appendQuotedString(target: StringBuilder, key: String): StringBuilder {
                target.append('"')
                (key.indices).map { i ->
                    when (val ch = key[i]) {
                        '\n' -> target.append("%0A")
                        '\r' -> target.append("%0D")
                        '"' -> target.append("%22")
                        else -> target.append(ch)
                    }
                }
                target.append('"')
                return target
            }
        }
    }

    class Builder {
        private val boundary: ByteString = uuid.toString().encodeUtf8()
        private val type = MIXED
        private val parts = mutableListOf<Part>()

        /**
         * Add a form data part to the body.
         */
        fun addFormDataPart(
            name: String,
            filename: String,
            url: String,
            callbacks: UploadCallbacks
        ): Builder {
            return addPart(Part.createFormData(name, filename, url, callbacks))
        }

        /**
         * Add a part to the body.
         */
        private fun addPart(part: Part): Builder {
            parts.add(part)
            return this
        }

        /**
         * Assemble the specified parts into a request body.
         */
        fun build(): ProgressRequestBody {
            check(parts.isNotEmpty()) { "Multipart body must have at least one part." }
            return ProgressRequestBody(boundary, type, parts)
        }
    }

    private inner class ProgressUpdater(
        private val mFilename: String,
        private val mUploaded: Long,
        private val mTotal: Long
    ) : Runnable {

        override fun run() {
            callbacks.onProgressUpdate(mFilename, (100 * mUploaded / mTotal).toInt())
        }
    }

    interface UploadCallbacks {
        fun onProgressUpdate(filename: String, percentage: Int)
    }
}