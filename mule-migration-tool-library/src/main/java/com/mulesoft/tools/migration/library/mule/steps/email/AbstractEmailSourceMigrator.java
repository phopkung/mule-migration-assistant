/*
 * Copyright (c) 2017 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master Subscription
 * Agreement (or other master license agreement) separately entered into in writing between
 * you and MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.tools.migration.library.mule.steps.email;

import static com.mulesoft.tools.migration.library.mule.steps.core.properties.InboundPropertiesHelper.addAttributesMapping;
import static com.mulesoft.tools.migration.step.util.TransportsUtils.migrateInboundEndpointStructure;

import com.mulesoft.tools.migration.step.category.MigrationReport;

import org.jdom2.Element;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Support for migrating sources of the email connector
 *
 * @author Mulesoft Inc.
 * @since 1.0.0
 */
public abstract class AbstractEmailSourceMigrator extends AbstractEmailMigrator {

  protected void addAttributesToInboundProperties(Element object, MigrationReport report) {
    migrateInboundEndpointStructure(getApplicationModel(), object, report, false);

    Map<String, String> expressionsPerProperty = new LinkedHashMap<>();
    expressionsPerProperty.put("toAddresses", "message.attributes.toAddresses joinBy ', '");
    expressionsPerProperty.put("ccAddresses", "message.attributes.ccAddresses joinBy ', '");
    expressionsPerProperty.put("bccAddresses", "message.attributes.bccAddresses joinBy ', '");
    expressionsPerProperty.put("replyToAddresses", "message.attributes.replyToAddresses joinBy ', '");
    expressionsPerProperty.put("fromAddresses", "message.attributes.fromAddresses joinBy ', '");

    expressionsPerProperty.put("subject", "if (message.attributes.subject != '') message.attributes.subject else '(no subject)'");
    expressionsPerProperty.put("contentType", "payload.^mimeType");

    expressionsPerProperty.put("sentDate", "message.attributes.sentDate");

    try {
      addAttributesMapping(getApplicationModel(), getInboundAttributesClass(),
                           expressionsPerProperty,
                           "message.attributes.headers mapObject ((value, key, index) -> { key : value })");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract String getInboundAttributesClass();

}