package cn.edu.sjtu.travelguide.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.entity.PoiVO;

public class RecommendListAdapter extends BaseQuickAdapter<PoiVO> {
    public RecommendListAdapter(Context context, List<PoiVO> data) {
        super(R.layout.recommend_item, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, PoiVO poiVO) {
        baseViewHolder.setText(R.id.recommend_title, poiVO.name);
        baseViewHolder.setText(R.id.recommend_address, poiVO.address);
        baseViewHolder.setText(R.id.recommend_phone, poiVO.phone);
        Glide.with(mContext)
                .load(poiVO.imgUrl)
                .error(android.R.color.black)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.imageview));//设置封面图片
    }

}
