package cn.com.modernmediausermodel.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.MultiComment;
import cn.com.modernmediausermodel.widget.CardDetailItemView;

public class CardDetailPagerAdapter extends PagerAdapter {

	private Context mContext;
	private MultiComment mComment = new MultiComment();
	private CardDetailItemView mCurrentCardDetatilItem;
	private Card mCard = new Card();

	public CardDetailPagerAdapter(Context context) {
		this.mContext = context;
	}

	public void setPagerData(Card card, MultiComment comment) {
		this.mCard = card;
		this.mComment = comment;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mCard.getCardItemList().size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		CardItem cardItem = mCard.getCardItemList().get(position);
		CardDetailItemView view = new CardDetailItemView(mContext);
		// 设置卡片内容
		String content = cardItem.getContents();
		String uid = cardItem.getType() == 2 ? cardItem.getFuid() : cardItem
				.getUid();
		view.setHeadData(content, mCard.getUserInfoMap().get(uid),
				cardItem.getTime());
		if (ParseUtil.listNotNull(mComment.getCommentList())) {
			view.getAdapter().setData(mComment.getCommentList().get(position),
					mComment.getUserInfoMap());
		}
		container.addView(view);
		return view;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		if (object instanceof CardDetailItemView) {
			mCurrentCardDetatilItem = (CardDetailItemView) object;
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE; // 使得调用notifyDataSetChanged有效
	}

	/**
	 * 取得当前显示的笔记详情页
	 * 
	 * @return
	 */
	public CardDetailItemView getCurrentCardDetatilItem() {
		return mCurrentCardDetatilItem;
	}
}
