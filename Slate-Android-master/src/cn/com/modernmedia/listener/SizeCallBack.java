package cn.com.modernmedia.listener;

/**
 * ��ҳ����ʱ�õ��ؼ��Ĵ�С
 * 
 * @author ZhuQiao
 * 
 */
public interface SizeCallBack {
	/**
	 * �������ͼǰ�����С
	 */
	public void onGlobalLayout();

	/**
	 * ָ����ͼ�ĳߴ�.
	 * 
	 * @param idx
	 *            view����.
	 * @param w
	 *            ����ͼ�Ŀ��.
	 * @param h
	 *            ����ͼ�ĸ߶�.
	 * @param dims
	 *            dims[0]ΪView��ȣ�dims[1]ΪView�߶�.
	 */
	public void getViewSize(int idx, int width, int height, int[] dims);
}
