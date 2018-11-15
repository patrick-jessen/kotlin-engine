package org.patrick.game.engine.ui

import glm_.vec2.Vec2

enum class UILayout {
    VERTICAL, HORIZONTAL
}

open class UIElement(
    private val prefSize:UISize,
    private val minSize:UISize,
    private val maxSize:UISize,
    private val layout:UILayout = UILayout.HORIZONTAL
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

    fun calculateSizes(avail:UISize) {
        val calcMinSize = calculateMinSize()
        val absPrefSize = prefSize.toAbsolute(avail)
        val absMaxSize = maxSize.toAbsolute(avail)

        if(!calcMinSize.fitsWithin(avail) || !calcMinSize.fitsWithin(absMaxSize))
            println("Warning: children do not fit")

        // Make this element fit within the available space
        size = absPrefSize
        size.fit(calcMinSize)
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
                        c.size.width++
                        done = false
                    }
                    if (extraSpace.height > 0 && childSize.height > c.size.height) {
                        extraSpace.height--
                        c.size.height++
                        done = false
                    }
                    c.calculateSizes(c.size)
                }
                if(done) break
            }


            // Position children
            var x = 0f
            for(c in children) {
                c.pos.x = x
                c.pos.y = (calcMinSize.height-c.size.height).toFloat()/2
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