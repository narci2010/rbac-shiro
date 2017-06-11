/*****************************************************************************
 * Copyright (c) 2013, theborakompanioni (http://www.example.org)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ****************************************************************************/
package rbac.thymeleaf.shiro.processor.attribute;

import rbac.thymeleaf.shiro.dialect.ShiroFacade;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractConditionalVisibilityAttrProcessor;
import org.thymeleaf.util.StringUtils;

import static rbac.thymeleaf.shiro.processor.AttributeUtils.getRawValue;

public class HasAllRolesAttrProcessor extends AbstractConditionalVisibilityAttrProcessor {

    public static HasAllRolesAttrProcessor create() {
        return new HasAllRolesAttrProcessor();
    }

    private static final String DELIMITER = ",";

    private static final String ATTRIBUTE_NAME = "hasAllRoles";
    private static final int PRECEDENCE = 300;

    protected HasAllRolesAttrProcessor() {
        super(ATTRIBUTE_NAME);
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE;
    }

    @Override
    protected boolean isVisible(final Arguments arguments, final Element element, final String attributeName) {
        String rawValue = getRawValue(element, attributeName);

        return ShiroFacade.hasAllRoles(StringUtils.split(rawValue, DELIMITER));
    }
}