package com.example.common.wight.spring.container

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.common.R

@Suppress("UNREACHABLE_CODE")
/**
 * Created by LL130386 on 2019/6/11.
 */
class CustomHeader : BaseHeader {
    private val TAG = CustomHeader::class.java.simpleName
    private val ROTATE_ANIM_DURATION = 180L
    private var arrowSrc: Int = 0
    private var rotateSrc: Int = 0
    private var mRotateUpAnim: RotateAnimation
    private var mRotateDownAnim: RotateAnimation
    private lateinit var headerTitle: TextView
    private lateinit var headerArrow: ImageView
    private lateinit var headerProgressbar: ProgressBar
    private lateinit var view: View
    //        lateinit var binding: LayoutRefreshHeaderBinding
    private var context: Context


    constructor(context: Context) : this(context, R.drawable.progress_small, R.drawable.ic_drop_ref)

    constructor(context: Context, rotationSrc: Int, arrowSrc: Int) {
        this.rotateSrc = rotationSrc
        this.arrowSrc = arrowSrc
        this.context = context

        mRotateUpAnim = RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateUpAnim.duration = ROTATE_ANIM_DURATION
        mRotateUpAnim.fillAfter = true

        mRotateDownAnim = RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateDownAnim.duration = ROTATE_ANIM_DURATION
        mRotateDownAnim.fillAfter = true
    }


    override fun getView(inflater: LayoutInflater, viewGroup: ViewGroup): View {
//        binding = DataBindingUtil.inflate(inflater, R.layout.layout_refresh_header, viewGroup, true)
//        headerTitle = binding.tvStatus
//        headerArrow = binding.ivIcon
//        headerProgressbar = binding.progressBar
//        headerArrow.setImageResource(arrowSrc)
//        return binding.root

        view = inflater.inflate(R.layout.layout_refresh_header, viewGroup, true)
        headerTitle = view.findViewById(R.id.tv_status)
        headerArrow = view.findViewById(R.id.iv_icon)
        headerProgressbar = view.findViewById(R.id.progress_bar)
        headerArrow.setImageResource(arrowSrc)

        return view
    }

    override fun onPreDrag(rootView: View) {
    }

    override fun onDropAnim(rootView: View, dy: Int) {
    }

    override fun onLimitDes(rootView: View, upOrDown: Boolean) {
        if (!upOrDown) {
            headerTitle.text = "释放立即刷新"
            if (headerArrow.visibility === View.VISIBLE) {
                headerArrow.setImageResource(R.drawable.ic_release_ref)
            }
        } else {
            headerTitle.text = "开始下拉刷新"
            if (headerArrow.visibility === View.VISIBLE) {
                headerArrow.startAnimation(mRotateDownAnim)
            }
        }
    }

    override fun onStartAnim() {
        headerTitle.text = "正在刷新..."
        headerArrow.visibility = View.INVISIBLE
        headerProgressbar.visibility = View.VISIBLE
    }

    override fun onFinishAnim() {
        headerArrow.setImageResource(R.drawable.ic_success_ref)
        headerArrow.visibility = View.VISIBLE
        headerProgressbar.visibility = View.INVISIBLE
        headerTitle.text = "刷新成功"
    }

    override fun onFinishRefresh() {
        if (headerArrow.visibility === View.VISIBLE) {
            headerArrow.setImageResource(R.drawable.ic_drop_ref)
        }
        headerTitle.text = "开始下拉刷新"
    }
}