package au.org.theark.study.web.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.DateValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.odlabs.wiquery.ui.themes.ThemeUiHelper;

import au.org.theark.core.vo.ModuleVO;
import au.org.theark.study.model.entity.Study;
import au.org.theark.study.model.entity.StudyStatus;
import au.org.theark.study.service.IStudyService;
import au.org.theark.study.web.Constants;
import au.org.theark.study.web.component.study.Details;
import au.org.theark.study.web.component.study.StudyModel;

@SuppressWarnings("serial")
public class StudyForm extends Form<StudyModel>{
	

	@SpringBean( name = Constants.STUDY_SERVICE)
	private IStudyService studyService;
	
	//Study Details Panel
	Details details;
	WebMarkupContainer  listContainer;
	private int mode;
	private TextField<String> studyIdTxtFld;
	private TextField<String> studyNameTxtFld;
	private TextArea<String> studyDescriptionTxtArea;
	private TextField<String> estYearOfCompletionTxtFld;
	private TextField<String> principalContactTxtFld;
	private TextField<String> principalContactPhoneTxtFld;
	private TextField<String> chiefInvestigatorTxtFld;
	private TextField<String> coInvestigatorTxtFld;
	private TextField<String> subjectKeyPrefixTxtFld;
	private TextField<Integer> subjectKeyStartAtTxtFld;
	private TextField<String> bioSpecimenPrefixTxtFld;
	private DatePicker<Date> dateOfApplicationDp;
	private DropDownChoice<StudyStatus> studyStatusDpChoices;
	private RadioChoice<Boolean> autoGenSubIdRdChoice;
	private RadioChoice<Boolean> autoConsentRdChoice;
	
	AjaxButton saveButton;
	AjaxButton cancelButton;
	AjaxButton deleteButton;
	
	List<ModuleVO> modules;
	
	protected void onSave(StudyModel studyModel, AjaxRequestTarget target){}
	
	protected  void onCancel(AjaxRequestTarget target){}
	
	protected void  onDelete(AjaxRequestTarget target){}
	

	
	/**
	 * Constructor: 
	 * @param id
	 * @param detailsPanel The panel that is linked to this Form instance
	 * @param container The WebMarkupContainer that will wrap the SearchResults
	 */
	public StudyForm(String id, Details detailsPanel, WebMarkupContainer container){
		
		super(id);
		
		listContainer = container;
		details = detailsPanel;
		
		cancelButton = new AjaxButton(Constants.CANCEL,  new StringResourceModel("cancelKey", this, null))
		{
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				listContainer.setVisible(false);
				details.setVisible(false);
				target.addComponent(details);
				target.addComponent(listContainer);
				details.getCpm().getObject().setStudy(new Study());
				onCancel(target);
			}
		};
		
		saveButton = new AjaxButton(Constants.SAVE, new StringResourceModel("saveKey", this, null))
		{
			public void onSubmit(AjaxRequestTarget target, Form<?> form)
			{	//Access the model associated using a reference to details panel
				CompoundPropertyModel<StudyModel> detailsCpm = details.getCpm();
				StudyModel model = detailsCpm.getObject();
				target.addComponent(details);
				onSave(model,target);
			}
		}; 
		
		deleteButton = new AjaxButton(Constants.DELETE, new StringResourceModel("deleteKey", this, null))
		{
			public void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				//Go to Search users page
				target.addComponent(details);
				onDelete(target);
			}
		};
		
		studyIdTxtFld =new TextField<String>(Constants.STUDY_KEY);
		studyNameTxtFld = new TextField<String>("study.name");
		studyDescriptionTxtArea = new TextArea<String>("study.description");
		estYearOfCompletionTxtFld = new TextField<String>("study.estimatedYearOfCompletion");
		principalContactTxtFld = new TextField<String>("study.contactPerson");
		principalContactPhoneTxtFld = new TextField<String>("study.contactPersonPhone");
		chiefInvestigatorTxtFld = new TextField<String>("study.chiefInvestigator");
		coInvestigatorTxtFld = new TextField<String>("study.coInvestigator");
		subjectKeyPrefixTxtFld = new TextField<String>("study.subjectIdPrefix");
		subjectKeyStartAtTxtFld = new TextField<Integer>("study.subjectKeyStart", Integer.class);
		bioSpecimenPrefixTxtFld = new TextField<String>("study.subStudyBiospecimenPrefix");
		dateOfApplicationDp = new DatePicker<Date>("study.dateOfApplication");
		
		CompoundPropertyModel<StudyModel> detailsCpm = details.getCpm();
		
		initStudyStatusDropDown(detailsCpm.getObject());
		autoGenSubIdRdChoice = initRadioButtonChoice(detailsCpm.getObject(),"study.autoGenerateSubjectKey","autoGenSubId");
		autoConsentRdChoice = initRadioButtonChoice(detailsCpm.getObject(),"study.autoConsent","autoConsent");
		attachValidation();
		
		if(mode == Constants.MODE_NEW){
			studyIdTxtFld.setEnabled(false);
		}else{
			studyIdTxtFld.setEnabled(false);
			studyNameTxtFld.setEnabled(false);
		}
		
		decorateComponents();
		addComponents();
		
	}
	

	public TextField<String> getStudyIdTxtFld() {
		return studyIdTxtFld;
	}
	
	public TextField<String> getStudyNameTxtFld() {
		return studyNameTxtFld;
	}
	
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	
	
	private void attachValidation(){
	
		studyNameTxtFld.setRequired(true).setLabel(new StringResourceModel("error.study.name.required", this, new Model<String>("Name")));
		//TODO Have to stop the validator posting the content with the error message
		studyDescriptionTxtArea.add(StringValidator.lengthBetween(1, 255));
		studyStatusDpChoices.setRequired(true).setLabel(new StringResourceModel("error.study.status.required",this, new Model<String>("Status")));
		dateOfApplicationDp.add(DateValidator.maximum(new Date())).setLabel( new StringResourceModel("error.study.doa.max.range",this, null));
		//Can be only today
		//Estimate year of completion - should be a valid year. Must be less than dateOfApplication
		chiefInvestigatorTxtFld.setRequired(true).setLabel(new StringResourceModel("error.study.chief",this,new Model<String>("Chief Investigator")));
		chiefInvestigatorTxtFld.add(StringValidator.lengthBetween(3, 50));
		
		coInvestigatorTxtFld.add(StringValidator.lengthBetween(3,50)).setLabel(new StringResourceModel("error.study.co.investigator",this, new Model<String>("Co Investigator")));
		//selectedApplicationsLmc.setRequired(true).setLabel( new StringResourceModel("error.study.selected.app", this, null));
		subjectKeyStartAtTxtFld.add( new RangeValidator<Integer>(1,Integer.MAX_VALUE)).setLabel( new StringResourceModel("error.study.subject.key.prefix", this, null));
	}
	
	private void decorateComponents(){
		ThemeUiHelper.componentRounded(studyIdTxtFld);
		
		ThemeUiHelper.componentRounded(studyNameTxtFld);
		ThemeUiHelper.componentRounded(studyDescriptionTxtArea);
		ThemeUiHelper.componentRounded(estYearOfCompletionTxtFld);
		ThemeUiHelper.componentRounded(studyStatusDpChoices);
		ThemeUiHelper.componentRounded(dateOfApplicationDp);
		ThemeUiHelper.componentRounded(principalContactPhoneTxtFld);
		ThemeUiHelper.componentRounded(principalContactTxtFld);
		ThemeUiHelper.componentRounded(chiefInvestigatorTxtFld);
		ThemeUiHelper.componentRounded(coInvestigatorTxtFld);
		ThemeUiHelper.componentRounded(subjectKeyPrefixTxtFld);
		ThemeUiHelper.componentRounded(subjectKeyStartAtTxtFld);
		ThemeUiHelper.componentRounded(bioSpecimenPrefixTxtFld);
		ThemeUiHelper.buttonRounded(saveButton);
		ThemeUiHelper.buttonRounded(cancelButton);
		ThemeUiHelper.buttonRounded(deleteButton);
	}
	
	private void addComponents(){
		add(studyIdTxtFld);
		add(studyNameTxtFld);
		add(studyDescriptionTxtArea);
		add(estYearOfCompletionTxtFld);
		add(studyStatusDpChoices);
		add(dateOfApplicationDp);
		add(principalContactPhoneTxtFld);
		add(principalContactTxtFld);
		add(chiefInvestigatorTxtFld);
		add(coInvestigatorTxtFld);
		add(subjectKeyPrefixTxtFld);
		add(subjectKeyStartAtTxtFld);
		add(bioSpecimenPrefixTxtFld);
		add(autoGenSubIdRdChoice);
		add(autoConsentRdChoice);
		add(saveButton);
		add(cancelButton.setDefaultFormProcessing(false));
		add(deleteButton);
	}
	
	@SuppressWarnings("unchecked")
	private void initStudyStatusDropDown(StudyModel study){
		List<StudyStatus>  studyStatusList = studyService.getListOfStudyStatus();
		ChoiceRenderer defaultChoiceRenderer = new ChoiceRenderer(Constants.NAME, Constants.STUDY_STATUS_KEY);
		PropertyModel propertyModel = new PropertyModel(study.getStudy(), Constants. STUDY_STATUS);
		studyStatusDpChoices = new DropDownChoice(Constants.STUDY_DROP_DOWN_CHOICE,propertyModel,studyStatusList,defaultChoiceRenderer);
	}
	/**
	 * A common method that can be used to render Yes/No using RadioChoice controls
	 * @param study
	 * @param propertyModelExpr
	 * @param radioChoiceId
	 * @return
	 */
	private RadioChoice<Boolean> initRadioButtonChoice(StudyModel study, String propertyModelExpr,String radioChoiceId){
	
		List<Boolean> list = new ArrayList<Boolean>();
		list.add(Boolean.TRUE);
		list.add(Boolean.FALSE);
		/* Implement the IChoiceRenderer*/
		
		IChoiceRenderer<Boolean> radioChoiceRender = new IChoiceRenderer<Boolean>() {
			public Object getDisplayValue(final Boolean choice){
				
				String displayValue=Constants.NO;
				
				if(choice !=null && choice.booleanValue()){
					displayValue = Constants.YES;
				}
				return displayValue;
			}
			
			public String getIdValue(final Boolean object,final int index){
				return object.toString();
			}
		};
		
		PropertyModel<Boolean> propertyModel = new PropertyModel<Boolean>(study,propertyModelExpr);
		return new RadioChoice<Boolean>(radioChoiceId,propertyModel,list,radioChoiceRender);
	}


}
