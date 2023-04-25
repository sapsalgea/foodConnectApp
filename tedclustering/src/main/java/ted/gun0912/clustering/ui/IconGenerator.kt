/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ted.gun0912.clustering.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ted.gun0912.clustering.R


/**
 * IconGenerator generates icons that contain text (or custom content) within an info
 * window-like shape.
 *
 *
 * The icon [Bitmap]s generated by the factory should be used in conjunction with a [ ].
 *
 *
 * This class is not thread safe.
 */
class IconGenerator(private val mContext: Context) {

    private val mContainer: ViewGroup =
        LayoutInflater.from(mContext).inflate(R.layout.amu_text_bubble, null) as ViewGroup
    private val mRotationLayout: RotationLayout
    private var mTextView: TextView? = null
    private var mContentView: View? = null

    private val mAnchorU = 0.5f
    private val mAnchorV = 1f
    private val mBackground: BubbleDrawable = BubbleDrawable(mContext.resources)

    init {
        mRotationLayout = mContainer.getChildAt(0) as RotationLayout
        mTextView = mRotationLayout.findViewById(R.id.amu_text)
        mContentView = mTextView
        setStyle(STYLE_DEFAULT)
    }

    /**
     * Sets the text content, then creates an icon with the current style.
     *
     * @param text the text content to display inside the icon.
     */
    fun makeIcon(text: CharSequence): Bitmap {
        if (mTextView != null) {
            mTextView!!.text = text
        }

        return makeIcon(mContainer)
    }

    /**
     * Sets the child view for the icon.
     *
     *
     * If the view contains a [TextView] with the id "text", operations such as [ ][.setTextAppearance] and [.makeIcon] will operate upon that [TextView].
     */
    fun setContentView(contentView: View) {
        mRotationLayout.removeAllViews()
        mRotationLayout.addView(contentView)
        mContentView = contentView
        val view = mRotationLayout.findViewById<View>(R.id.amu_text)
        mTextView = if (view is TextView) view else null
    }


    /**
     * Sets the text color, size, style, hint color, and highlight color from the specified
     * `TextAppearance` resource.
     *
     * @param resId the identifier of the resource.
     */
    fun setTextAppearance(context: Context, resId: Int) {
        if (mTextView != null) {
            mTextView!!.setTextAppearance(context, resId)
        }
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color from the specified
     * `TextAppearance` resource.
     *
     * @param resid the identifier of the resource.
     */
    fun setTextAppearance(resid: Int) {
        setTextAppearance(mContext, resid)
    }

    /**
     * Sets the style of the icon. The style consists of a background and text appearance.
     */
    fun setStyle(style: Int) {
        setColor(getStyleColor(style))
        setTextAppearance(mContext, getTextStyle(style))
    }

    /**
     * Sets the background to the default, with a given color tint.
     *
     * @param color the color for the background tint.
     */
    fun setColor(color: Int) {
        mBackground.setColor(color)
        setBackground(mBackground)
    }

    /**
     * Set the background to a given Drawable, or remove the background.
     *
     * @param background the Drawable to use as the background, or null to remove the background.
     */
    // View#setBackgroundDrawable is compatible with pre-API level 16 (Jelly Bean).
    fun setBackground(background: Drawable?) {
        mContainer.setBackgroundDrawable(background)

        // Force setting of padding.
        // setBackgroundDrawable does not call setPadding if the background has 0 padding.
        if (background != null) {
            val rect = Rect()
            background.getPadding(rect)
            mContainer.setPadding(rect.left, rect.top, rect.right, rect.bottom)
        } else {
            mContainer.setPadding(0, 0, 0, 0)
        }
    }

    /**
     * Sets the padding of the content view. The default padding of the content view (i.e. text
     * view) is 5dp top/bottom and 10dp left/right.
     *
     * @param left   the left padding in pixels.
     * @param top    the top padding in pixels.
     * @param right  the right padding in pixels.
     * @param bottom the bottom padding in pixels.
     */
    fun setContentPadding(left: Int, top: Int, right: Int, bottom: Int) {
        mContentView!!.setPadding(left, top, right, bottom)
    }

    companion object {

        val STYLE_DEFAULT = 1
        val STYLE_WHITE = 2
        val STYLE_RED = 3
        val STYLE_BLUE = 4
        val STYLE_GREEN = 5
        val STYLE_PURPLE = 6
        val STYLE_ORANGE = 7

        private fun getStyleColor(style: Int): Int {
            when (style) {
                STYLE_DEFAULT, STYLE_WHITE -> return -0x1
                STYLE_RED -> return -0x340000
                STYLE_BLUE -> return -0xff6634
                STYLE_GREEN -> return -0x996700
                STYLE_PURPLE -> return -0x66cc34
                STYLE_ORANGE -> return -0x7800
                else -> return -0x1
            }
        }

        private fun getTextStyle(style: Int): Int {
            when (style) {
                STYLE_DEFAULT, STYLE_WHITE -> return R.style.amu_Bubble_TextAppearance_Dark
                STYLE_RED, STYLE_BLUE, STYLE_GREEN, STYLE_PURPLE, STYLE_ORANGE -> return R.style.amu_Bubble_TextAppearance_Light
                else -> return R.style.amu_Bubble_TextAppearance_Dark
            }
        }


        /**
         * Creates an icon with the current content and style.
         *
         *
         * This method is useful if a custom view has previously been set, or if text content is not
         * applicable.
         */
        fun makeIcon(view: View): Bitmap {
            val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(measureSpec, measureSpec)

            var measuredWidth = view.measuredWidth
            var measuredHeight = view.measuredHeight

            view.layout(0, 0, measuredWidth, measuredHeight)
            val r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            r.eraseColor(Color.TRANSPARENT)

            val canvas = Canvas(r)

            view.draw(canvas)
            return r
        }

    }
}
