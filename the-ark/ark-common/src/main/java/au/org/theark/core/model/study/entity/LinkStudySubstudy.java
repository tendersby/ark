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
package au.org.theark.core.model.study.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import au.org.theark.core.Constants;

/**
 * LinkStudySubstudy entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "LINK_STUDY_SUBSTUDY", schema = Constants.STUDY_SCHEMA)
public class LinkStudySubstudy implements java.io.Serializable {

	// Fields

	private Long	id;
	private Study	studyByStudyKey;
	private Study	studyBySubstudyKey;

	// Constructors

	/** default constructor */
	public LinkStudySubstudy() {
	}

	/** minimal constructor */
	public LinkStudySubstudy(Long id) {
		this.id = id;
	}

	/** full constructor */
	public LinkStudySubstudy(Long id, Study studyByStudyKey, Study studyBySubstudyKey) {
		this.id = id;
		this.studyByStudyKey = studyByStudyKey;
		this.studyBySubstudyKey = studyBySubstudyKey;
	}

	// Property accessors
	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STUDY_ID")
	public Study getStudyByStudyKey() {
		return this.studyByStudyKey;
	}

	public void setStudyByStudyKey(Study studyByStudyKey) {
		this.studyByStudyKey = studyByStudyKey;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_STUDY_ID")
	public Study getStudyBySubstudyKey() {
		return this.studyBySubstudyKey;
	}

	public void setStudyBySubstudyKey(Study studyBySubstudyKey) {
		this.studyBySubstudyKey = studyBySubstudyKey;
	}

}
