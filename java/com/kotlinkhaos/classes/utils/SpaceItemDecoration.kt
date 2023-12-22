import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * An ItemDecoration for RecyclerView that adds space above and below each item.
 *
 * @param spaceHeight The height of the space to be added above and below each item, in pixels.
 */
class SpaceItemDecorationHeight(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = spaceHeight;
        outRect.bottom = spaceHeight;
    }
}

/**
 * An ItemDecoration for RecyclerView that adds space below each item, unless it's the last item.
 * in which case no space is added.
 *
 * @param spaceHeight The height of the space to be added below each item, in pixels.
 */
class SpaceItemDecorationBottom(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Check if the item is the last one
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = spaceHeight
        } else {
            // Last item, no space added
            outRect.bottom = 0
        }
    }
}