package com.uns.test.printer

class EscPosPrinter {

    companion object {
        private const val ESC: Byte = 0x1B.toByte()
        private const val GS: Byte = 0x1D.toByte()
        public const val ALIGMNET_CENTER: Int= 1;
        public const val ALIGMNET_LEFT: Int= 0;
        public const val ALIGMNET_RIGHT: Int = 2;
        public const val UNDERLINE_OFF: Int = 0;
        public const val UNDERLINE_ON: Int = 1;

        public const val ROTATION_0: Int = 0;
        public const val ROTATION_90: Int = 1;
        public const val ROTATION_180: Int = 2;
        public const val ROTATION_270: Int = 3;

        public const val FONT_Character_A: Int = 0;
        public const val FONT_Character_B: Int = 1;
        public const val FONT_Character_C: Int = 2;
        public const val FONT_Character_D: Int = 3;

        public const val UP_DOWN_OFF: Int = 0;
        public const val UP_DOWN_ON: Int = 1;

        public const val CHARACTER_SIZE_NORMAL: Int = 0;
        public const val CHARACTER_SIZE_LARGE_: Int = 1;

        public const val CODE_BAR_EAN13: Byte = 2;
        public const val CODE_BAR_EAN8: Byte = 1;
        public const val CODE_BAR_UPCA: Byte = 3;
        public const val CODE_BAR_UPCE: Byte = 4;
        public const val CODE_BAR_CODE39: Byte = 5;
        public const val CODE_BAR_CODE93: Byte = 72;
        public const val CODE_BAR_CODE128: Byte = 73;
        public const val CODE_BAR_CODABAR: Byte = 71;

    }

    fun initializePrinter(): ByteArray {
        return byteArrayOf(ESC, '@'.code.toByte())
    }

    fun printText(text: String): ByteArray {
        return text.toByteArray()
    }


    fun feedPaper(lines: Int): ByteArray {
        return byteArrayOf(ESC, 'd'.code.toByte(), lines.toByte())
    }


    fun setAlignment(alignment: Int): ByteArray {
        return byteArrayOf(ESC, 'a'.code.toByte(), alignment.toByte())
    }

    //no
    fun setPrintDirection(direction: Int): ByteArray {
        return byteArrayOf(ESC, 'T'.code.toByte(), direction.toByte())
    }

    fun setPrintRotation(rotation: Int): ByteArray {
        return byteArrayOf(ESC, 'V'.code.toByte(), rotation.toByte())
    }
    fun setPrintFontCharacter(fontCharacter: Int): ByteArray {
        return byteArrayOf(ESC, 'M'.code.toByte(), fontCharacter.toByte())
    }

    fun setPrintUpsideDown(upsideDown: Int): ByteArray {
        return byteArrayOf(ESC, '{'.code.toByte(), upsideDown.toByte())
    }
    fun setCharacterSize(characterSize: Int): ByteArray {
        return byteArrayOf(GS, '!'.code.toByte(), characterSize.toByte())
    }


    fun setUnderLine(strokeLevel: Int): ByteArray {
        return byteArrayOf(ESC, '-'.code.toByte(), strokeLevel.toByte())
    }


    fun printReceiptString(): ByteArray{
        val builder = StringBuilder()

        // Encabezado del ticket
       /* builder.append("\u001B\u0040") // Inicializar la impresora
        builder.append("\u001B\u0061\u0001") // Alinear al centro
        builder.append("\u001B\u0045\u0001") // Activar negrita
        builder.append("Supermercado XYZ\n")
        builder.append("\u001B\u0045\u0000") // Desactivar negrita*/
        builder.append("Av. Principal 123\nCiudad, País\nTel: +123456789\n\n")

        // Encabezados de la tabla
        /*builder.append("\u001B\u0061\u0000") // Alinear a la izquierda
        builder.append("\u001B\u0045\u0001") // Activar negrita*/
        builder.append("Codigo  Producto         Cant.   Precio  Total\n")
       // builder.append("\u001B\u0045\u0000") // Desactivar negrita
        builder.append("--------------------------------------------\n")

        // Productos
        for (i in 1..30) {
            val codigo = "000$i".padEnd(6)
            val producto = "Producto $i".padEnd(18)
            val cantidad = "1".padEnd(7)
            val precioUnitario = "10.00".padEnd(8)
            val precioTotal = "10.00"
            builder.append("$codigo$producto$cantidad$precioUnitario$precioTotal\n")
        }

        // Total
        builder.append("--------------------------------------------\n")
       // builder.append("\u001B\u0061\u0002") // Alinear a la derecha
      //  builder.append("\u001B\u0045\u0001") // Activar negrita
        builder.append("Total: 300.00\n")
     //   builder.append("\u001B\u0045\u0000") // Desactivar negrita

        // Mensaje final
       // builder.append("\u001B\u0061\u0001") // Alinear al centro
        builder.append("Gracias por su compra\n")
        builder.append("Vuelva pronto\n\n")

        // Cortar papel
      //  builder.append("\u001D\u0056\u0041\u0000")

        return builder.toString().toByteArray()
    }

    fun printBarcodeAndText( barcodeType: Byte): ByteArray {
        val command = mutableListOf<ByteArray>()

        // Inicializar la impresora
        command.add(initializePrinter())
        // Alinear el texto al centro
        command.add(setAlignment(ALIGMNET_CENTER))

        // Comando para imprimir código de barras EAN13

        val barcodeData = "123456789012" // Datos de ejemplo para EAN13

        // Agregar GS k m
        command.add(byteArrayOf(GS, 'k'.code.toByte(), barcodeType,barcodeData.toByte()))


        // Agregar datos del código de barras
        for (char in barcodeData.toString()) {
            command.add(byteArrayOf(char.code.toByte()))
        }

        // Agregar NUL (fin de los datos del código de barras)
        command.add(byteArrayOf(0x00.toByte()))

        // Imprimir texto debajo del código de barras
        command.add(setAlignment(ALIGMNET_CENTER))
       // command.addAll("\n$barcodeData\n\n".toByteArray().toList())

        // Cortar papel
       /* command.add(0x1D.toByte()) // GS
        command.add(0x56.toByte()) // 'V'
        command.add(0x41.toByte()) // 'A'
        command.add(0x00.toByte()) // Corte completo*/
        val combinedSize = command.sumOf { it.size }
        val result = ByteArray(combinedSize)
        var currentPosition = 0

        for (byteArray in command) {
            System.arraycopy(byteArray, 0, result, currentPosition, byteArray.size)
            currentPosition += byteArray.size
        }

        return result

    }

    public fun printQRCodeCommand(data: String): ByteArray {
        val command = mutableListOf<ByteArray>()

        // Inicializar la impresora
        command.add(byteArrayOf(0x1B, '@'.toByte())) // ESC @ (Initialize the printer)

        // Alinear el texto al centro
        command.add(byteArrayOf(0x1B, 'a'.toByte(), 1)) // ESC a 1 (Align center)

        // Modelo QR: 49 (0x31) - Seleccionar el modelo de QR Code
        // GS ( k pL pH cn fn n
        command.add(byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00))
        // 1D 28 6B: Introducción del comando
        // 04 00: Longitud del parámetro (4 bytes)
        // 31 41: Identificación del comando para seleccionar el modelo
        // 32: Modelo 2 (el valor puede ser 49 o 50, según el modelo de QR soportado por la impresora)
        // 00: Terminador

        // Tamaño de módulo QR: 3 - Establecer el tamaño del módulo
        // GS ( k pL pH cn fn n
        command.add(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, 0x03))
        // 1D 28 6B: Introducción del comando
        // 03 00: Longitud del parámetro (3 bytes)
        // 31 43: Identificación del comando para establecer el tamaño del módulo
        // 03: Tamaño del módulo (puede variar de 1 a 16)

        // Nivel de corrección de errores QR: 48 (0) - Establecer el nivel de corrección de errores
        // GS ( k pL pH cn fn n
        command.add(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30))
        // 1D 28 6B: Introducción del comando
        // 03 00: Longitud del parámetro (3 bytes)
        // 31 45: Identificación del comando para establecer el nivel de corrección de errores
        // 30: Nivel L (puede ser 48, 49, 50, 51 para niveles L, M, Q, H respectivamente)

        // Almacenar datos QR
        val dataBytes = data.toByteArray() // Convertir los datos a un arreglo de bytes
        val dataLength = dataBytes.size + 3 // Longitud de los datos más 3 bytes para el comando
        val pL = (dataLength and 0xFF).toByte() // Longitud baja
        val pH = ((dataLength shr 8) and 0xFF).toByte() // Longitud alta
        command.add(byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30) + dataBytes)
        // 1D 28 6B: Introducción del comando
        // pL pH: Longitud del parámetro (datos de longitud baja y alta)
        // 31 50 30: Identificación del comando para almacenar datos
        // dataBytes: Datos del QR

        // Imprimir código QR
        // GS ( k pL pH cn fn m
        command.add(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))
        // 1D 28 6B: Introducción del comando
        // 03 00: Longitud del parámetro (3 bytes)
        // 31 51: Identificación del comando para imprimir el QR
        // 30: Imprimir

        // Cortar papel
        command.add(byteArrayOf(0x1D, 0x56, 0x41, 0x00)) // GS V A 0 (Cut paper)

        val combinedSize = command.sumOf { it.size }
        val result = ByteArray(combinedSize)
        var currentPosition = 0

        for (byteArray in command) {
            System.arraycopy(byteArray, 0, result, currentPosition, byteArray.size)
            currentPosition += byteArray.size
        }

        return result
    }


}
