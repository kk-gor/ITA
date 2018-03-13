package ua.itatool.vivewmodel;

import android.databinding.ObservableField;
import android.view.View;

/**
 * Created by djdf.crash on 07.03.2018.
 */

public class MainModelBinding {

    private ObservableField<Integer> showProgress = new ObservableField<>(View.GONE);
    private ObservableField<String> updateInfo = new ObservableField<>();
    private ObservableField<String> articleText = new ObservableField<>();

    public ObservableField<Integer> getShowProgress() {
        return showProgress;
    }

    public void setShowProgress(ObservableField<Integer> showProgress) {
        this.showProgress = showProgress;
    }

    public ObservableField<String> getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(ObservableField<String> updateInfo) {
        this.updateInfo = updateInfo;
    }

    public ObservableField<String> getArticleText() {
        return articleText;
    }

    public void setArticleText(ObservableField<String> articleText) {
        this.articleText = articleText;
    }

}
