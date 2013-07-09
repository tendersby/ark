package au.org.theark.study.web.component.pedigree;

import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import au.org.theark.core.vo.ArkCrudContainerVO;
import au.org.theark.study.model.vo.PedigreeVo;
import au.org.theark.study.model.vo.RelationshipVo;
import au.org.theark.study.web.Constants;
import au.org.theark.study.web.component.pedigree.form.ContainerForm;
import au.org.theark.study.web.component.pedigree.form.SearchForm;

public class SearchPanel extends Panel {

	private static final long				serialVersionUID	= 1L;

	private ArkCrudContainerVO				arkCrudContainerVO;
	private FeedbackPanel					feedBackPanel;
	private PageableListView<RelationshipVo>	listView;

	public SearchPanel(String id, ArkCrudContainerVO crudContainerVO, FeedbackPanel feedBackPanel, ContainerForm containerForm, PageableListView<RelationshipVo> listView) {
		super(id);
		arkCrudContainerVO = crudContainerVO;
		this.feedBackPanel = feedBackPanel;
		this.listView = listView;
	}

	public void initialisePanel(CompoundPropertyModel<PedigreeVo> studyCompCpm) {

		SearchForm searchStudyCompForm = new SearchForm(Constants.SEARCH_FORM, arkCrudContainerVO, feedBackPanel);
		add(searchStudyCompForm);
	}

}