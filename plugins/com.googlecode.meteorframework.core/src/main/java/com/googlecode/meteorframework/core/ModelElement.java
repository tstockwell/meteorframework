package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.Model;


@Model public interface ModelElement
{
	Set<Deployment> getDeploymentTypes();
}
