/*******************************************************************************
 * Copyright (c) 2011  University of Western Australia. All rights reserved.
 * 
 * This file is part of The Ark.
 * 
 * The Ark is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * The Ark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package au.org.theark.study.web.component.subjectcustomdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import au.org.theark.core.exception.ArkSystemException;
import au.org.theark.core.model.study.entity.CustomFieldCategory;
import au.org.theark.core.model.study.entity.CustomFieldType;
import au.org.theark.core.model.study.entity.Study;
import au.org.theark.core.security.ArkPermissionHelper;
import au.org.theark.core.service.IArkCommonService;
import au.org.theark.core.util.CustomFieldCategoryOrderingHelper;
import au.org.theark.core.web.component.customfield.dataentry.AbstractCustomDataEditorForm;
import au.org.theark.study.model.vo.SubjectCustomDataVO;
import au.org.theark.study.web.component.subjectcustomdata.form.CustomDataEditorForm;

/**
 * @author elam
 * 
 */
@SuppressWarnings({ "serial" })
public class SubjectCustomDataEditorPanel extends Panel {

	private static final long serialVersionUID = -1L;

	protected CompoundPropertyModel<SubjectCustomDataVO> cpModel;
	@SpringBean(name = au.org.theark.core.Constants.ARK_COMMON_SERVICE)
	protected IArkCommonService<Void>							iArkCommonService;

	
	protected FeedbackPanel feedbackPanel;
	protected AbstractCustomDataEditorForm<SubjectCustomDataVO> customDataEditorForm;
	protected SubjectCustomDataDataViewPanel dataViewPanel;
	protected Label warnSaveLabel;
	private DropDownChoice<CustomFieldCategory>		customeFieldCategoryDdc;

	public SubjectCustomDataEditorPanel(String id, CompoundPropertyModel<SubjectCustomDataVO> cpModel, FeedbackPanel feedBackPanel) {
		super(id);

		this.cpModel = cpModel;
		this.feedbackPanel = feedBackPanel;
	}

	public SubjectCustomDataEditorPanel initialisePanel() {
		
		customDataEditorForm = new CustomDataEditorForm("customDataEditorForm", cpModel, feedbackPanel).initialiseForm();
		
		Collection<CustomFieldCategory> customFieldCategoryCollection=getAvailableAllCategoryListInStudyByCustomFieldType();
		List<CustomFieldCategory> customFieldCatLst=CustomFieldCategoryOrderingHelper.getInstance().orderHierarchicalyCustomFieldCategories((List<CustomFieldCategory>)customFieldCategoryCollection);
		ChoiceRenderer customfieldCategoryRenderer = new ChoiceRenderer(Constants.CUSTOMFIELDCATEGORY_NAME, Constants.CUSTOMFIELDCATEGORY_ID){
						@Override
						public Object getDisplayValue(Object object) {
						CustomFieldCategory cuscat=(CustomFieldCategory)object;
							return CustomFieldCategoryOrderingHelper.getInstance().preTextDecider(cuscat)+ super.getDisplayValue(object);
						}
		};
		customeFieldCategoryDdc = new DropDownChoice<CustomFieldCategory>(Constants.FIELDVO_CUSTOMFIELD_CUSTOEMFIELDCATEGORY, customFieldCatLst, customfieldCategoryRenderer);
		customeFieldCategoryDdc.setOutputMarkupId(true);
		customeFieldCategoryDdc.setNullValid(true);
		customeFieldCategoryDdc.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				
				customDataEditorForm.getDataViewWMC().remove(dataViewPanel);
				dataViewPanel = new SubjectCustomDataDataViewPanel("dataViewPanel", cpModel).initialisePanel(null,customeFieldCategoryDdc.getModelObject());
				customDataEditorForm.getDataViewWMC().add(dataViewPanel);
				target.add(dataViewPanel);
				target.add(customDataEditorForm);
			}
		});
		//dataViewPanel = new SubjectCustomDataDataViewPanel("dataViewPanel", cpModel).initialisePanel(null);
		//initialise
		dataViewPanel = new SubjectCustomDataDataViewPanel("dataViewPanel", cpModel).initialisePanel(null,customeFieldCategoryDdc.getModelObject());
		
		AjaxPagingNavigator pageNavigator = new AjaxPagingNavigator("navigator", dataViewPanel.getDataView()) {
			@Override
			protected void onAjaxEvent(AjaxRequestTarget target) {
				target.add(customDataEditorForm.getDataViewWMC());
				target.add(this);
			}
		};
		pageNavigator.setVisible(false);
		customDataEditorForm.add(customeFieldCategoryDdc);
		customDataEditorForm.getDataViewWMC().add(dataViewPanel);

		warnSaveLabel = new Label("warnSaveLabel", new ResourceModel("warnSaveLabel"));
		warnSaveLabel.setVisible(ArkPermissionHelper.isActionPermitted(Constants.NEW));

		add(customDataEditorForm);
		add(pageNavigator);
		add(warnSaveLabel);

		return this;
	}
	/**
	 * Get custom field category collection from model.
	 * @return
	 */
	private Collection<CustomFieldCategory> getAvailableAllCategoryListInStudyByCustomFieldType(){
		
		Study study =cpModel.getObject().getLinkSubjectStudy().getStudy();
		
		CustomFieldType customFieldType=iArkCommonService.getCustomFieldTypeByName(au.org.theark.core.Constants.SUBJECT);
		Collection<CustomFieldCategory> customFieldCategoryCollection = null;
		try {
			customFieldCategoryCollection =  iArkCommonService.getAvailableAllCategoryListInStudyByCustomFieldType(study, customFieldType);
		} catch (ArkSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return customFieldCategoryCollection;
	}

}
