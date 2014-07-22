package cn.com.modernmediasolo.unit;


public class SoloHelper {
	/**
	 * 
	 * @param fromOffset
	 * @param toOffset
	 * @param offset
	 * @return
	 */
	public static String checkSelection(String fromOffset, String toOffset,
			String offsetName, boolean containFL) {
		String greaterOperate = containFL ? " >= " : " > ";
		String smallerOperate = containFL ? " <= " : " < ";
		String result = "";
		// TODO 因为要取文章的范围，必须包含第一篇和最后一篇
		String greaterThanFrom = " and " + offsetName + greaterOperate + "'"
				+ fromOffset + "'";
		String smallerThanTo = " and " + offsetName + smallerOperate + "'"
				+ toOffset + "'";
		if (fromOffset.compareTo("0") == 0 && toOffset.compareTo("0") == 0) {
			// 0_0 获取全部
			return result;
		}
		if (fromOffset.compareTo("0") > 0 && toOffset.compareTo("0") > 0) {
			// from_to 获取中间
			result += greaterThanFrom;
			result += smallerThanTo;
			return result;
		}
		if (fromOffset.compareTo("0") > 0) {
			// from_0
			result += greaterThanFrom;
		} else if (toOffset.compareTo("0") > 0) {
			// 0_to
			result += smallerThanTo;
		}
		return result;
	}

}
