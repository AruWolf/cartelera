package com.litvy.carteleria.util.network

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileOutputStream

class LocalHttpServer(
    private val context: Context,
    port: Int = 8080
) : NanoHTTPD(port) {

    private val resourcesDir: File by lazy {
        File(context.filesDir, "resources").apply {
            if (!exists()) mkdirs()
        }
    }

    override fun serve(session: IHTTPSession): Response {

        return when (session.method) {

            Method.GET -> handleGet()

            Method.POST -> handlePost(session)

            else -> newFixedLengthResponse("Método no soportado")
        }
    }

    private fun handleGet(): Response {

        val folders = resourcesDir.listFiles()
            ?.filter { it.isDirectory }
            ?.joinToString("") { folder ->
                "<option value='${folder.name}'>${folder.name}</option>"
            } ?: ""

        val html = """
            <html>
            <head>
                <style>
                    body {
                        background-color: #121212;
                        color: white;
                        font-family: Arial;
                        text-align: center;
                    }
                    .container {
                        margin-top: 40px;
                    }
                    input, select {
                        margin: 10px;
                        padding: 8px;
                        font-size: 16px;
                    }
                    button {
                        padding: 10px 20px;
                        font-size: 18px;
                        background-color: #4CAF50;
                        border: none;
                        color: white;
                        border-radius: 8px;
                        cursor: pointer;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Panel Cartelería TV</h2>

                    <form method="post" enctype="multipart/form-data">
                        <div>
                            <input type="text" name="newFolder" placeholder="Nueva carpeta (opcional)" />
                        </div>

                        <div>
                            <select name="folder">
                                $folders
                            </select>
                        </div>

                        <div>
                            <input type="file" name="file"/>
                        </div>

                        <div>
                            <button type="submit">Subir archivo</button>
                        </div>
                    </form>
                </div>
            </body>
            </html>
        """.trimIndent()

        return newFixedLengthResponse(Response.Status.OK, "text/html", html)
    }

    private fun handlePost(session: IHTTPSession): Response {

        val files = HashMap<String, String>()
        session.parseBody(files)

        val params = session.parameters

        val newFolderName = params["newFolder"]?.firstOrNull()?.trim()
        val selectedFolder = params["folder"]?.firstOrNull()?.trim()
        val uploadedFilePath = files["file"]

        var targetFolder: File? = null

        // Crear nueva carpeta si se indicó
        if (!newFolderName.isNullOrEmpty()) {
            targetFolder = File(resourcesDir, newFolderName)
            if (!targetFolder.exists()) targetFolder.mkdirs()
        } else if (!selectedFolder.isNullOrEmpty()) {
            targetFolder = File(resourcesDir, selectedFolder)
        }

        if (uploadedFilePath != null && targetFolder != null) {

            val uploadedFile = File(uploadedFilePath)
            val targetFile = File(targetFolder, uploadedFile.name)

            FileOutputStream(targetFile).use { output ->
                uploadedFile.inputStream().copyTo(output)
            }

            return newFixedLengthResponse(
                Response.Status.OK,
                "text/plain",
                "Archivo subido correctamente a carpeta: ${targetFolder.name}"
            )
        }

        return newFixedLengthResponse("Error al subir archivo")
    }
}
