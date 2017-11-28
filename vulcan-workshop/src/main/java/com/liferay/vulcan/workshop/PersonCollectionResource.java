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

import static com.liferay.vulcan.result.Try.fromFallible;

import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
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

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class PersonCollectionResource
	implements CollectionResource<User, LongIdentifier> {

	@Override
	public Representor<User, LongIdentifier> buildRepresentor(
		Representor.Builder<User, LongIdentifier> representorBuilder) {

		return representorBuilder.types(
			"Person"
		).identifier(
			user -> user::getUserId
		).addDate(
			"birthDate", user -> fromFallible(user::getBirthday).orElse(null)
		).addString(
			"additionalName", User::getMiddleName
		).addString(
			"alternateName", User::getScreenName
		).addString(
			"email", User::getEmailAddress
		).addString(
			"familyName", User::getLastName
		).addString(
			"gender",
			user -> fromFallible(user::isMale).map(
				male -> male ? "male" : "female"
			).orElse(
				null
			)
		).addString(
			"givenName", User::getFirstName
		).addString(
			"jobTitle", User::getJobTitle
		).addString(
			"name", User::getFullName
		).build();
	}

	@Override
	public String getName() {
		return "people";
	}

	@Override
	public Routes<User> routes(
		RoutesBuilder<User, LongIdentifier> routesBuilder) {

		return routesBuilder.addCollectionPageGetter(
			this::_getPageItems, RootIdentifier.class, Company.class
		).addCollectionPageItemGetter(
			this::_getUser
		).build();
	}

	private PageItems<User> _getPageItems(
		Pagination pagination, RootIdentifier rootIdentifier, Company company) {

		List<User> users;

		try {
			users = _userService.getCompanyUsers(
				company.getCompanyId(), pagination.getStartPosition(),
				pagination.getEndPosition());
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}

		int count = _userLocalService.getCompanyUsersCount(
			company.getCompanyId());

		return new PageItems<>(users, count);
	}

	private User _getUser(LongIdentifier userLongIdentifier) {
		try {
			return _userService.getUserById(userLongIdentifier.getId());
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException(
				"Unable to get user " + userLongIdentifier.getId(), e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}