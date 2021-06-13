package caprita.catalin.cityquest.ui.models.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;

public class QuestTypeMarker implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final QuestBriefModel questBriefModel;
    private final int imageResId;

    public QuestTypeMarker(QuestBriefModel questBriefModel) {
        this.questBriefModel = questBriefModel;
        this.position = new LatLng(questBriefModel.getLocationLat().doubleValue(),
                questBriefModel.getLocationLng().doubleValue());
        this.title = questBriefModel.getTitle();
        this.snippet = questBriefModel.getLocationName();
        QuestType type = QuestType.valueOf(questBriefModel.getType());
        switch (type) {
            case QUIZ:
                this.imageResId = R.drawable.quest_quiz;
                break;
            case GUESSTIMATE:
                this.imageResId = R.drawable.quiz_guess;
                break;
            default:
                this.imageResId = R.drawable.quest_sns;
                break;
        }
    }


    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public QuestBriefModel getQuestBriefModel() {
        return questBriefModel;
    }

    public int getImageResId() {
        return imageResId;
    }
}
