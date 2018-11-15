package org.patrick.game.engine.ui

import glm_.vec2.Vec2

enum class UILayout {
    VERTICAL, HORIZONTAL
}
enum class UIAlign(internal val x:Int, internal val y:Int) {
    TOP_LEFT(0,0), TOP_CENTER(1,0), TOP_RIGHT(2,0),
    MIDDLE_LEFT(0,1), MIDDLE_CENTER(1,1), MIDDLE_RIGHT(2, 1),
    BOTTOM_LEFT(0, 2), BOTTOM_CENTER(1,2), BOTTOM_RIGHT(2,2)
}

open class UIElement(
    private val prefSize:UISize,
    private val minSize:UISize = prefSize,
    private val maxSize:UISize = prefSize,
    private val layout:UILayout = UILayout.HORIZONTAL,
    private val align:UIAlign = UIAlign.MIDDLE_CENTER
) {
    protected var size = UISize()
    private var pos = Vec2()
    private val children = mutableListOf<UIElement>()

    private fun calculateMinSize(includeSelf:Boolean = false):UISize {
        size = UISize()
        for(c in children) {
            val cMin = c.calculateMinSize(true)
            size.width += cMin.width
            if(cMin.height > size.height)
                size.height = cMin.height
        }

        if(includeSelf)
            size.fit(minSize.toAbsolute(UISize()))
        return size
    }

    internal fun calculateSizes(avail:UISize) {
        val calcMinSize = calculateMinSize()
        val absMinSize = minSize.toAbsolute(avail)
        val absPrefSize = prefSize.toAbsolute(avail)
        val absMaxSize = maxSize.toAbsolute(avail)

        if(!calcMinSize.fitsWithin(avail) || !calcMinSize.fitsWithin(absMaxSize))
            println("Warning: children do not fit")

        // Make this element fit within the available space
        size = absPrefSize
        size.fit(calcMinSize)
        size.fit(absMinSize)
        size.fitWithin(absMaxSize)
        size.fitWithin(avail)

        // Determine the size of children
        if(children.isNotEmpty()) {
            // Resize children
            val extraSpace = size - calcMinSize
            while(extraSpace.width > 0 || extraSpace.height > 0) {
                var done = true

                for (c in children) {
                    val childSize = c.prefSize.toAbsolute(size)
                    if (extraSpace.width > 0 && childSize.width > c.size.width) {
                        extraSpace.width--
                        calcMinSize.width++
                        c.size.width++
                        done = false
                    }
                    if (size.height > c.size.height && childSize.height > c.size.height) {
                        extraSpace.height--
                        c.size.height++
                        done = false
                    }
                }
                if(done) break
            }
            for(c in children) {
                c.calculateSizes(c.size)
            }


            // Position children
            var x = 0f
            for(c in children) {
                c.pos.x = when(align.x) {
                    1 -> (size.width - calcMinSize.width).toFloat()/2 + x
                    2 -> (size.width - calcMinSize.width).toFloat() + x
                    else -> x
                }
                c.pos.y = when(align.y) {
                    1 -> (size.height - c.size.height).toFloat()/2
                    2 -> (size.height - c.size.height).toFloat()
                    else -> 0f
                }

                x += c.size.width
            }
        }
    }

    fun add(child: UIElement):UIElement {
        children.add(child)
        return child
    }

    fun render(parentPos:Vec2 = Vec2()) {
        draw(parentPos + pos)
        for (c in children)
            c.render(parentPos + pos)
    }
    protected open fun draw(pos:Vec2) {}
}