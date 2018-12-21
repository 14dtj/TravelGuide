package cn.edu.sjtu.travelguide.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.entity.SearchRecord;

public class RecommendListAdapter extends BaseQuickAdapter<SearchRecord> {
    private Context context;

    public RecommendListAdapter(Context context, List<SearchRecord> data) {
        super(R.layout.recommend_item, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, SearchRecord poiVO) {
        baseViewHolder.setText(R.id.recommend_title, poiVO.getName());
        baseViewHolder.setText(R.id.recommend_address, poiVO.getAddress());
        baseViewHolder.setText(R.id.recommend_phone, poiVO.getPhone_num());
        baseViewHolder.setText(R.id.recommend_detail_url, poiVO.getDetail_url());
        Glide.with(mContext)
                .load(poiVO.getImg_url())
                .error(R.drawable.default_recommend)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.imageview));//设置封面图片
    }

}
