package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ParseUtil;

public class TestActivity extends Activity {
	private Context mContext;
	private EditText issue, column, article, app;
	private Button button, appButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		mContext = this;
		issue = (EditText) findViewById(R.id.issue_et);
		column = (EditText) findViewById(R.id.column_et);
		article = (EditText) findViewById(R.id.article_et);
		app = (EditText) findViewById(R.id.appid_et);
		button = (Button) findViewById(R.id.edit_ok);
		appButton = (Button) findViewById(R.id.edit_app_ok);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DataHelper.setIssueId(mContext,
						ParseUtil.stoi(issue.getText().toString(), -1));
				DataHelper.setColumnUpdateTime(mContext,
						ParseUtil.stoi(column.getText().toString(), -1));
				DataHelper.setArticleUpdateTime(mContext,
						ParseUtil.stoi(article.getText().toString(), -1));
				finish();
			}
		});
		appButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DataHelper.setAppId(mContext,
						ParseUtil.stoi(app.getText().toString(), -1));
				finish();
			}
		});
	}

}
