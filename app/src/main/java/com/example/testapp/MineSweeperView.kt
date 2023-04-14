package com.example.testapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.random.Random
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener


class MineSweeperView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var boardWidth = 12
    private var boardHeight = 22
    private var mineCount = (boardWidth * boardHeight * 0.10).toInt()
    private var cellSize = 100

    private var boardPixelWidth = 0
    private var boardPixelHeight = 0

    private var horizontalOffset = 0f
    private var verticalOffset = 0f


    private lateinit var cells: Array<Array<Cell>>
    private var bitmapMine: Bitmap? = null
    private var bitmapFlag: Bitmap? = null

    private var score = 0


    private val paintLine = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 48f
        strokeWidth = 5f
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
    }

    private val paintTextBlue = Paint().apply {
        color = Color.BLUE
        textSize = 48f
        strokeWidth = 5f
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
    }

    private val paintTextGreen = Paint().apply {
        color = Color.GREEN
        textSize = 48f
        strokeWidth = 5f
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
    }

    private val paintTextRed = Paint().apply {
        color = Color.RED
        textSize = 48f
        strokeWidth = 5f
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
    }


    private val paintCell = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        generateBoard() // Initialize the cells property
    }

    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val x = ((e.x - horizontalOffset) / cellSize).toInt()
            val y = ((e.y - verticalOffset) / cellSize).toInt()

            if (x in 0 until boardWidth && y in 0 until boardHeight) {
                val cell = cells[x][y]
                if (!cell.isRevealed && !cell.isFlagged) {
                    revealCell(x, y)
                    checkWinCondition()
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val x = ((e.x - horizontalOffset) / cellSize).toInt()
            val y = ((e.y - verticalOffset) / cellSize).toInt()

            if (x in 0 until boardWidth && y in 0 until boardHeight) {
                val cell = cells[x][y]
                if (!cell.isRevealed) {
                    cell.isFlagged = !cell.isFlagged
                    invalidate()
                }
            }
        }
    })

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calcular el tamaño de las celdas en función del tamaño de la pantalla y de las celdas deseadas
        val screenWidth = width
        val screenHeight = height
        cellSize = min(screenWidth / boardWidth, screenHeight / boardHeight)

        // Calcular el tamaño total del tablero en píxeles
        boardPixelWidth = boardWidth * cellSize
        boardPixelHeight = boardHeight * cellSize

        // Calcular el desplazamiento horizontal y vertical
        horizontalOffset = (width - boardPixelWidth) / 2f
        verticalOffset = (height - boardPixelHeight) / 2f

        // Cargar y escalar la imagen de la mina
        bitmapMine = BitmapFactory.decodeResource(resources, R.drawable.mine)
        bitmapMine = Bitmap.createScaledBitmap(bitmapMine!!, cellSize, cellSize, false)

        // Cargar y escalar la imagen de la bandera
        bitmapFlag = BitmapFactory.decodeResource(resources, R.drawable.flag)
        bitmapFlag = Bitmap.createScaledBitmap(bitmapFlag!!, cellSize, cellSize, false)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {

            verticalOffset = (height - boardPixelHeight) / 2f
            horizontalOffset = (width - boardPixelWidth) / 2f

            for (x in 0 until boardWidth) {
                for (y in 0 until boardHeight) {
                    val cell = cells[x][y]
                    if (cell.isRevealed) {
                        if (cell.isMine) {
                            drawBitmap(
                                bitmapMine!!,
                                null,
                                Rect(
                                    (horizontalOffset + x * cellSize).toInt(),
                                    (verticalOffset + y * cellSize).toInt(),
                                    (horizontalOffset + (x + 1) * cellSize).toInt(),
                                    (verticalOffset + (y + 1) * cellSize).toInt()
                                ),
                                null
                            )
                        } else {
                            drawRect(
                                horizontalOffset + (x * cellSize),
                                verticalOffset + (y * cellSize),
                                horizontalOffset + ((x + 1) * cellSize),
                                verticalOffset + ((y + 1) * cellSize),
                                paintCell
                            )

                            if (cell.mineCount > 0) {
                                val paint = when (cell.mineCount) {
                                    1 -> paintTextBlue
                                    2 -> paintTextGreen
                                    3 -> paintTextRed
                                    else -> paintText
                                }
                                drawText(
                                    cell.mineCount.toString(),
                                    horizontalOffset + (x * cellSize) + (cellSize / 2),
                                    verticalOffset + (y * cellSize) + (cellSize / 2) - (paint.ascent() + paint.descent()) / 2,
                                    paint
                                )
                            }
                        }
                    } else if (cell.isFlagged) {
                        drawBitmap(
                            bitmapFlag!!,
                            null,
                            Rect(
                                (horizontalOffset + x * cellSize).toInt(),
                                (verticalOffset + y * cellSize).toInt(),
                                (horizontalOffset + (x + 1) * cellSize).toInt(),
                                (verticalOffset + (y + 1) * cellSize).toInt()
                            ),
                            null
                        )
                    }
                    drawRect(
                        horizontalOffset + (x * cellSize),
                        verticalOffset + (y * cellSize),
                        horizontalOffset + ((x + 1) * cellSize),
                        verticalOffset + ((y + 1) * cellSize),
                        paintLine
                    )
                }
            }
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return true
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }


    private fun revealCell(x: Int, y: Int) {
        val cell = cells[x][y]
        if (cell.isMine) {
            // Game over
            cells.forEach { row ->
                row.forEach { c ->
                    c.isRevealed = true
                }
            }
            gameEndListener?.onGameEnd() // Añade esta línea
            invalidate()
        } else {
            if (!cell.isRevealed) { // Asegúrate de que la celda no haya sido revelada previamente
                cell.isRevealed = true
                score++ // Aumenta la puntuación en 1
                scoreChangeListener?.onScoreChanged(score) // Notifica al listener

                if (cell.mineCount == 0) {
                    // Reveal adjacent cells
                    for (xx in x - 1..x + 1) {
                        for (yy in y - 1..y + 1) {
                            if (xx in 0 until boardWidth && yy in 0 until boardHeight && !cells[xx][yy].isRevealed) {
                                revealCell(xx, yy)
                            }
                        }
                    }
                }
                invalidate()
            }
        }
    }


    private fun checkWinCondition() {
        var revealedCount = 0
        cells.forEach { row ->
            row.forEach { c ->
                if (c.isRevealed) {
                    revealedCount++
                }
            }
        }
        if (revealedCount == boardWidth * boardHeight - mineCount) {
            // Game won
            cells.forEach { row ->
                row.forEach { c ->
                    c.isRevealed = true
                }
            }
            gameEndListener?.onGameEnd() // Añade esta línea
            invalidate()
        }
    }

    private fun generateBoard() {
        cells = Array(boardWidth) { x ->
            Array(boardHeight) { y ->
                Cell(x, y, isMine = false, isRevealed = false, mineCount = 0)
            }
        }
        repeat(mineCount) {
            var x: Int
            var y: Int
            do {
                x = Random.nextInt(boardWidth)
                y = Random.nextInt(boardHeight)
            } while (cells[x][y].isMine)
            cells[x][y].isMine = true
            for (xx in x - 1..x + 1) {
                for (yy in y - 1..y + 1) {
                    if (xx in 0 until boardWidth && yy in 0 until boardHeight) {
                        cells[xx][yy].mineCount++
                    }
                }
            }
        }

    }

    fun resetGame() {
        generateBoard()
        score = 0 // Añadir esta línea para restablecer la puntuación
        scoreChangeListener?.onScoreChanged(score) // Notifica al listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmapMine?.recycle()
        bitmapFlag?.recycle()
    }


    data class Cell(
        val x: Int,
        val y: Int,
        var isMine: Boolean,
        var isRevealed: Boolean,
        var mineCount: Int,
        var isFlagged: Boolean = false
    )

    interface OnScoreChangeListener {
        fun onScoreChanged(score: Int)
    }

    private var scoreChangeListener: OnScoreChangeListener? = null

    fun setOnScoreChangeListener(listener: OnScoreChangeListener) {
        scoreChangeListener = listener
    }

    interface OnGameEndListener {
        fun onGameEnd()
    }

    private var gameEndListener: OnGameEndListener? = null

    fun setOnGameEndListener(listener: OnGameEndListener) {
        gameEndListener = listener
    }

}

