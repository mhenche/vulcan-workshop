/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.vulcan.workshop;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.vulcan.resource.CollectionResource;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.resource.identifier.LongIdentifier;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true, service = CollectionResource.class)
public class BlogPostingResource
	implements CollectionResource<BlogsEntry, LongIdentifier> {

	@Override
	public Representor<BlogsEntry, LongIdentifier> buildRepresentor(
		Representor.Builder<BlogsEntry, LongIdentifier> representorBuilder) {

		return representorBuilder.types(
			"BlogPosting"
		).identifier(
			blogsEntry -> blogsEntry::getEntryId
		).build();
	}

	@Override
	public String getName() {
		return "blog-postings";
	}

	@Override
	public Routes<BlogsEntry> routes(
		RoutesBuilder<BlogsEntry, LongIdentifier> routesBuilder) {

		return null;
	}

}