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

import com.liferay.blogs.kernel.exception.NoSuchEntryException;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryService;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.CollectionResource;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.resource.identifier.LongIdentifier;
import com.liferay.vulcan.resource.identifier.RootIdentifier;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
		).addDate(
			"createDate", BlogsEntry::getCreateDate
		).addDate(
			"displayDate", BlogsEntry::getDisplayDate
		).addDate(
			"modifiedDate", BlogsEntry::getModifiedDate
		).addDate(
			"publishedDate", BlogsEntry::getLastPublishDate
		).addEmbeddedModel(
			"creator", User.class, this::_getUserOptional
		).addLink(
			"license", "https://creativecommons.org/licenses/by/4.0"
		).addString(
			"alternativeHeadline", BlogsEntry::getSubtitle
		).addString(
			"articleBody", BlogsEntry::getContent
		).addString(
			"description", BlogsEntry::getDescription
		).addString(
			"fileFormat", blogsEntry -> "text/html"
		).addString(
			"headline", BlogsEntry::getTitle
		).build();
	}

	@Override
	public String getName() {
		return "blog-postings";
	}

	@Override
	public Routes<BlogsEntry> routes(
		RoutesBuilder<BlogsEntry, LongIdentifier> routesBuilder) {

		return routesBuilder.addCollectionPageGetter(
			this::_getPageItems, RootIdentifier.class
		).addCollectionPageItemGetter(
			this::_getBlogsEntry
		).build();
	}

	private BlogsEntry _getBlogsEntry(
		LongIdentifier blogsEntryIdLongIdentifier) {

		try {
			return _blogsService.getEntry(blogsEntryIdLongIdentifier.getId());
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException(
				"Unable to get blogs entry " +
					blogsEntryIdLongIdentifier.getId(),
				e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<BlogsEntry> _getPageItems(
		Pagination pagination, RootIdentifier rootIdentifier) {

		List<BlogsEntry> blogsEntries;

		try {
			blogsEntries = _blogsService.getGroupEntries(
				20143, 0, pagination.getStartPosition(),
				pagination.getEndPosition());
		}
		catch (SecurityException se) {
			throw new NotAuthorizedException(se);
		}

		int count = _blogsService.getGroupEntriesCount(20143, 0);

		return new PageItems<>(blogsEntries, count);
	}

	private Optional<User> _getUserOptional(BlogsEntry blogsEntry) {
		try {
			return Optional.ofNullable(
				_userService.getUserById(blogsEntry.getUserId()));
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException(
				"Unable to get user " + blogsEntry.getUserId(), e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private BlogsEntryService _blogsService;

	@Reference
	private UserService _userService;

}