package marxo.entity.report;

import marxo.entity.action.Content;

public class PageRecord extends Record {
	int submissionCount = 0;
	int viewCount = 0;

	public static PageRecord getInstance(Content content) {
		assert content.getType().equals(Content.Type.PAGE);

		PageRecord pageRecord = new PageRecord();

		pageRecord.viewCount = content.getViewCount();
		pageRecord.submissionCount = content.submissions.size();

		return pageRecord;
	}
}
