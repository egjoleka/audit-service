package com.izettle.assignment.ddo;

import java.util.List;

import com.izettle.assignment.entity.LoginAudit;

public class AuditsDisplayEntity {

	private List<LoginAudit> loginAudits;

	public AuditsDisplayEntity() {

	}

	public AuditsDisplayEntity(List<LoginAudit> loginAudits) {
		this.loginAudits = loginAudits;
	}

	public List<LoginAudit> getLoginAudits() {
		return loginAudits;
	}

	public void setLoginAudits(List<LoginAudit> loginAudits) {
		this.loginAudits = loginAudits;
	}

	@Override
	public String toString() {
		return "Audits [loginAudits=" + loginAudits + "]";
	}

}
