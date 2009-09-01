CREATE OR REPLACE FORCE VIEW "WAGERLAB"."IX_PATIENTV2" ("DELETED", "PATIENTKEY", "OLDPATIENTKEY", "PATIENTID", "DOB", "MIDDLENAME", "SURNAME", "FIRSTNAME", "SPECIES", "ADDR_LINE1", "ADDR_POSTCODE", "ADDR_STATE", "ADDR_COUNTRY", "SEX", "ADDR_SUBURB", "TITLE", "USER", "STATUS", "EMAIL", "TELEPHONE_MOBILE", "TELEPHONE_WORK", "TELEPHONE_HOME", "FLAG", "ADDR_OTHER_STATE", "ADDR_OTHER_COUNTRY", "CAUSEOD", "DODEATH", "MEDICARENO", "COMMENTS", "STUDYKEY", "FAMILYID", "OTHERID") AS 
  SELECT 0       AS deleted,
  s.subjectkey AS patientkey,
  s.oldkey     AS oldpatientkey,
  s.subjectid  AS patientid,
  s.dob        AS dob,
  s.middlename,
  s.surname,
  s.firstname,
  'Human'     AS species,
  addr_street AS addr_line1,
  addr_postcode,
  addr_state,
  addr_country,
  DECODE(sex, 'M', 'Male', 'F', 'Female', 'U', 'Unknown') sex,
  addr_suburb,
  title,
  NULL "USER",
 decode(cs.status,0,'Not Consented',1,'Consented',2,'Refused',3,'Withdrawn') status,
  email,
  mobile_phone telephone_mobile,
  work_phone telephone_work,
  home_phone telephone_home,
  NULL flag,
  NULL addr_other_state,
  NULL addr_other_country,
  cause_of_death causeod,
  date_of_death dodeath,
  NULL medicareno,
  s.comments,
  s.studykey,
  familyid,
  asrbno AS otherid
FROM zeus.subject s,
  zeus.consent_study cs,
  (select css.status, css.subjectkey from zeus.consent_section cst, zeus.consent_subject_section css where cst.consentsectionkey = css.consentsectionkey
   and cst.section = 'consent_biospec') cst
WHERE cs.subjectkey       = s.subjectkey
AND cst.subjectkey (+)    = s.subjectkey;
 
