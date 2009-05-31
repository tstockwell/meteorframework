package com.googlecode.meteorframework;

import java.util.Set;

import com.googlecode.meteorframework.annotation.Model;


@Model public interface ModelElement
{
	Set<Deployment> getDeploymentTypes();
}
