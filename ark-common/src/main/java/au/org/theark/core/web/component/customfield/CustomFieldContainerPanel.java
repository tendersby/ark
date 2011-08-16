package au.org.theark.core.web.component.customfield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.theark.core.model.study.entity.ArkModule;
import au.org.theark.core.model.study.entity.CustomField;
import au.org.theark.core.model.study.entity.Study;
import au.org.theark.core.service.IArkCommonService;
import au.org.theark.core.vo.CustomFieldVO;
import au.org.theark.core.web.component.AbstractContainerPanel;
import au.org.theark.core.web.component.ArkDataProvider;
import au.org.theark.core.web.component.customfield.form.ContainerForm;

/**
 * @author nivedann
 * 
 */
@SuppressWarnings("unchecked")
public class CustomFieldContainerPanel extends AbstractContainerPanel<CustomFieldVO> {
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -1L;
	private static final Logger		log					= LoggerFactory.getLogger(CustomFieldContainerPanel.class);
	
//	private DetailPanel					detailsPanel;
	private ContainerForm				containerForm;

	private WebMarkupContainer			arkContextMarkup;

	@SpringBean(name = au.org.theark.core.Constants.ARK_COMMON_SERVICE)
	private IArkCommonService			iArkCommonService;

	private DataView<CustomField>		dataView;
	private ArkDataProvider<CustomField, IArkCommonService>	customFieldProvider;

	/**
	 * @param id
	 */
	public CustomFieldContainerPanel(String id, WebMarkupContainer arkContextMarkup, boolean useCustomFieldDisplay) {

		super(id, true);
		this.arkContextMarkup = arkContextMarkup;
		/* Initialise the CPM */
		cpModel = new CompoundPropertyModel<CustomFieldVO>(new CustomFieldVO());
		cpModel.getObject().setUseCustomFieldDisplay(useCustomFieldDisplay);
		
		containerForm = new ContainerForm("containerForm", cpModel);
		containerForm.add(initialiseFeedBackPanel());
		containerForm.add(initialiseDetailPanel());
		containerForm.add(initialiseSearchResults());
		containerForm.add(initialiseSearchPanel());

		prerenderContextCheck();
		
		add(containerForm);
	}

	protected void prerenderContextCheck() {
		// Get the Study and Module out of context
		Long sessionStudyId = (Long) SecurityUtils.getSubject().getSession().getAttribute(au.org.theark.core.Constants.STUDY_CONTEXT_ID);
		Long sessionModuleId = (Long) SecurityUtils.getSubject().getSession().getAttribute(au.org.theark.core.Constants.ARK_MODULE_KEY);

		if ((sessionStudyId != null) && (sessionModuleId != null)) {
			ArkModule arkModule = null;
			Study study = null;
			study = iArkCommonService.getStudy(sessionStudyId);
			arkModule = iArkCommonService.getArkModuleById(sessionModuleId);
			
			if (study != null && arkModule != null) {
				cpModel.getObject().getCustomField().setStudy(study);
				cpModel.getObject().getCustomField().setArkModule(arkModule);
			}
		}
	}

	protected WebMarkupContainer initialiseSearchPanel() {
		
		Long sessionStudyId = (Long) SecurityUtils.getSubject().getSession().getAttribute(au.org.theark.core.Constants.STUDY_CONTEXT_ID);
		if (sessionStudyId != null && sessionStudyId > 0) {
			containerForm.getModelObject().getCustomField().setStudy(iArkCommonService.getStudy(sessionStudyId));
		}

		SearchPanel searchPanel = new SearchPanel("searchComponentPanel", cpModel, arkCrudContainerVO, feedBackPanel);

		searchPanel.initialisePanel();
		arkCrudContainerVO.getSearchPanelContainer().add(searchPanel);
		return arkCrudContainerVO.getSearchPanelContainer();
	}

	protected WebMarkupContainer initialiseDetailPanel() {

//		detailsPanel = new DetailPanel("detailsPanel", feedBackPanel, searchResultPanelContainer, detailPanelContainer, detailPanelFormContainer, searchPanelContainer, viewButtonContainer,
//				editButtonContainer, arkContextMarkup, containerForm);
//		detailsPanel.initialisePanel();
		Panel detailsPanel = new EmptyPanel("detailsPanel");
		detailsPanel.setOutputMarkupPlaceholderTag(true);	//ensure this is replaceable
		arkCrudContainerVO.getDetailPanelContainer().add(detailsPanel);
		return arkCrudContainerVO.getDetailPanelContainer();
	}

	protected WebMarkupContainer initialiseSearchResults() {

		SearchResultListPanel searchResultListPanel = new SearchResultListPanel("searchResults", cpModel, arkCrudContainerVO, feedBackPanel);

		// Data providor to paginate resultList
		customFieldProvider = new ArkDataProvider<CustomField, IArkCommonService>(iArkCommonService) {

			public int size() {
				return service.getCustomFieldCount(model.getObject());
			}

			public Iterator<CustomField> iterator(int first, int count) {
				List<CustomField> listSubjects = new ArrayList<CustomField>();
				if (isActionPermitted()) {
					listSubjects = service.searchPageableCustomFields(model.getObject(), first, count);
				}
				return listSubjects.iterator();
			}
		};
		// Set the criteria for the data provider
		customFieldProvider.setModel(new LoadableDetachableModel<CustomField>() {
			@Override
			protected CustomField load() {
				return cpModel.getObject().getCustomField();
			}
		});

		dataView = searchResultListPanel.buildDataView(customFieldProvider);
		dataView.setItemsPerPage(au.org.theark.core.Constants.ROWS_PER_PAGE);

		AjaxPagingNavigator pageNavigator = new AjaxPagingNavigator("navigator", dataView) {
			@Override
			protected void onAjaxEvent(AjaxRequestTarget target) {
				target.addComponent(arkCrudContainerVO.getSearchResultPanelContainer());
			}
		};
		searchResultListPanel.add(pageNavigator);
		searchResultListPanel.add(dataView);
		arkCrudContainerVO.getSearchResultPanelContainer().add(searchResultListPanel);
		return arkCrudContainerVO.getSearchResultPanelContainer();
	}

	public void resetDataProvider() {
	}


}
