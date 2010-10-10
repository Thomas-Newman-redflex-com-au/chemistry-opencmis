/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.client.runtime.repository;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.ChangeEvent;
import org.apache.chemistry.opencmis.client.api.ChangeEvents;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.runtime.ChangeEventImpl;
import org.apache.chemistry.opencmis.client.runtime.ChangeEventsImpl;
import org.apache.chemistry.opencmis.client.runtime.PersistentDocumentImpl;
import org.apache.chemistry.opencmis.client.runtime.PersistentFolderImpl;
import org.apache.chemistry.opencmis.client.runtime.PersistentPolicyImpl;
import org.apache.chemistry.opencmis.client.runtime.PersistentPropertyImpl;
import org.apache.chemistry.opencmis.client.runtime.PersistentRelationshipImpl;
import org.apache.chemistry.opencmis.client.runtime.PersistentSessionImpl;
import org.apache.chemistry.opencmis.client.runtime.QueryResultImpl;
import org.apache.chemistry.opencmis.client.runtime.RenditionImpl;
import org.apache.chemistry.opencmis.client.runtime.objecttype.DocumentTypeImpl;
import org.apache.chemistry.opencmis.client.runtime.objecttype.FolderTypeImpl;
import org.apache.chemistry.opencmis.client.runtime.objecttype.PolicyTypeImpl;
import org.apache.chemistry.opencmis.client.runtime.objecttype.RelationshipTypeImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.PropertyId;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.definitions.DocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.FolderTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PolicyTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyBooleanDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDateTimeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDecimalDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyHtmlDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyIdDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyIntegerDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyStringDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyUriDefinition;
import org.apache.chemistry.opencmis.commons.definitions.RelationshipTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.ChangeType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.spi.BindingsObjectFactory;

/**
 * Persistent model object factory.
 */
public class PersistentObjectFactoryImpl implements ObjectFactory, Serializable {

    private static final long serialVersionUID = 1L;

    private PersistentSessionImpl session = null;

    /**
     * Constructor.
     */
    protected PersistentObjectFactoryImpl(PersistentSessionImpl session) {
        if (session == null) {
            throw new IllegalArgumentException("Session must be set!");
        }

        this.session = session;
    }

    /**
     * Creates a new factory instance.
     */
    public static ObjectFactory newInstance(PersistentSessionImpl session) {
        return new PersistentObjectFactoryImpl(session);
    }

    /**
     * Returns the bindings object factory.
     */
    protected BindingsObjectFactory getBindingsObjectFactory() {
        return session.getBinding().getObjectFactory();
    }

    // ACL and ACE

    public Acl convertAces(List<Ace> aces) {
        if (aces == null) {
            return null;
        }

        BindingsObjectFactory bof = getBindingsObjectFactory();

        List<Ace> providerAces = new ArrayList<Ace>();
        for (Ace ace : aces) {
            providerAces.add(bof.createAccessControlEntry(ace.getPrincipalId(), ace.getPermissions()));
        }

        return bof.createAccessControlList(providerAces);
    }

    public Ace createAce(String principal, List<String> permissions) {
        BindingsObjectFactory bof = getBindingsObjectFactory();

        Ace ace = bof.createAccessControlEntry(principal, permissions);

        return ace;
    }

    public Acl createAcl(List<Ace> aces) {
        BindingsObjectFactory bof = getBindingsObjectFactory();

        Acl acl = bof.createAccessControlList(aces);

        return acl;
    }

    // policies

    public List<String> convertPolicies(List<Policy> policies) {
        if (policies == null) {
            return null;
        }

        List<String> result = new ArrayList<String>();

        for (Policy policy : policies) {
            if ((policy != null) && (policy.getId() != null)) {
                result.add(policy.getId());
            }
        }

        return result;
    }

    // renditions

    public Rendition convertRendition(String objectId, RenditionData rendition) {
        if (rendition == null) {
            throw new IllegalArgumentException("Rendition must be set!");
        }

        // TODO: what should happen if the length is not set?
        long length = (rendition.getBigLength() == null ? -1 : rendition.getBigLength().longValue());
        int height = (rendition.getBigHeight() == null ? -1 : rendition.getBigHeight().intValue());
        int width = (rendition.getBigWidth() == null ? -1 : rendition.getBigWidth().intValue());

        return new RenditionImpl(this.session, objectId, rendition.getStreamId(), rendition.getRenditionDocumentId(),
                rendition.getKind(), length, rendition.getMimeType(), rendition.getTitle(), height, width);
    }

    // content stream

    public ContentStream createContentStream(String filename, long length, String mimetype, InputStream stream) {
        return new ContentStreamImpl(filename, BigInteger.valueOf(length), mimetype, stream);
    }

    public ContentStream convertContentStream(ContentStream contentStream) {
        if (contentStream == null) {
            return null;
        }

        BigInteger length = (contentStream.getLength() < 0 ? null : BigInteger.valueOf(contentStream.getLength()));

        return getBindingsObjectFactory().createContentStream(contentStream.getFileName(), length,
                contentStream.getMimeType(), contentStream.getStream());
    }

    // types

    public ObjectType convertTypeDefinition(TypeDefinition typeDefinition) {
        if (typeDefinition instanceof DocumentTypeDefinition) {
            return new DocumentTypeImpl(this.session, (DocumentTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof FolderTypeDefinition) {
            return new FolderTypeImpl(this.session, (FolderTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof RelationshipTypeDefinition) {
            return new RelationshipTypeImpl(this.session, (RelationshipTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof PolicyTypeDefinition) {
            return new PolicyTypeImpl(this.session, (PolicyTypeDefinition) typeDefinition);
        } else {
            throw new CmisRuntimeException("Unknown base type!");
        }
    }

    public ObjectType getTypeFromObjectData(ObjectData objectData) {
        if ((objectData == null) || (objectData.getProperties() == null)
                || (objectData.getProperties().getProperties() == null)) {
            return null;
        }

        PropertyData<?> typeProperty = objectData.getProperties().getProperties().get(PropertyIds.OBJECT_TYPE_ID);
        if (!(typeProperty instanceof PropertyId)) {
            return null;
        }

        return this.session.getTypeDefinition((String) typeProperty.getFirstValue());
    }

    // properties

    public <T> Property<T> createProperty(PropertyDefinition<T> type, List<T> values) {
        return new PersistentPropertyImpl<T>(type, values);
    }

    @SuppressWarnings("unchecked")
    protected <T> Property<T> convertProperty(ObjectType objectType,
            PropertyData<T> pd) {
        PropertyDefinition<T> definition = (PropertyDefinition<T>) objectType.getPropertyDefinitions().get(
                pd.getId());
        if (definition == null) {
            // property without definition
            throw new CmisRuntimeException("Property '" + pd.getId()
                    + "' doesn't exist!");
        }
        return createProperty(definition, pd.getValues());
    }

    public Map<String, Property<?>> convertProperties(ObjectType objectType, Properties properties) {
        // check input
        if (objectType == null) {
            throw new IllegalArgumentException("Object type must set!");
        }

        if (objectType.getPropertyDefinitions() == null) {
            throw new IllegalArgumentException("Object type has no property defintions!");
        }

        if ((properties == null) || (properties.getProperties() == null)) {
            throw new IllegalArgumentException("Properties must be set!");
        }

        // iterate through properties and convert them
        Map<String, Property<?>> result = new LinkedHashMap<String, Property<?>>();
        for (Map.Entry<String, PropertyData<?>> entry : properties.getProperties().entrySet()) {
            // find property definition
            Property<?> apiProperty = convertProperty(objectType, entry.getValue());
            result.put(entry.getKey(), apiProperty);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public Properties convertProperties(Map<String, ?> properties, ObjectType type, Set<Updatability> updatabilityFilter) {
        // check input
        if (properties == null) {
            return null;
        }

        // get the type
        if (type == null) {
            Object typeId = properties.get(PropertyIds.OBJECT_TYPE_ID);
            if (!(typeId instanceof String)) {
                throw new IllegalArgumentException("Type or type property must be set!");
            }

            type = session.getTypeDefinition(typeId.toString());
        }

        // some preparation
        BindingsObjectFactory bof = getBindingsObjectFactory();
        List<PropertyData<?>> propertyList = new ArrayList<PropertyData<?>>();

        // the big loop
        for (Map.Entry<String, ?> property : properties.entrySet()) {
            if ((property == null) || (property.getKey() == null)) {
                continue;
            }

            String id = property.getKey();
            Object value = property.getValue();

            if (value instanceof Property<?>) {
                Property<?> p = (Property<?>) value;
                if (!id.equals(p.getId())) {
                    throw new IllegalArgumentException("Property id mismatch: '" + id + "' != '" + p.getId() + "'!");
                }
                value = (p.getDefinition().getCardinality() == Cardinality.SINGLE ? p.getFirstValue() : p.getValues());
            }

            // get the property definition
            PropertyDefinition<?> definition = type.getPropertyDefinitions().get(id);
            if (definition == null) {
                throw new IllegalArgumentException("Property +'" + id + "' is not valid for this type!");
            }

            // check updatability
            if (updatabilityFilter != null) {
                if (!updatabilityFilter.contains(definition.getUpdatability())) {
                    continue;
                }
            }

            // single and multi value check
            List<?> values;
            if (value == null) {
                values = null;
            } else if (value instanceof List<?>) {
                if (definition.getCardinality() != Cardinality.MULTI) {
                    throw new IllegalArgumentException("Property '" + id + "' is not a multi value property!");
                }
                values = (List<?>) value;

                // check if the list is homogeneous and does not contain null
                // values
                Class<?> valueClazz = null;
                for (Object o : values) {
                    if (o == null) {
                        throw new IllegalArgumentException("Property '" + id + "' contains null values!");
                    }
                    if (valueClazz == null) {
                        valueClazz = o.getClass();
                    } else {
                        if (!valueClazz.isInstance(o)) {
                            throw new IllegalArgumentException("Property '" + id + "' is inhomogeneous!");
                        }
                    }
                }
            } else {
                if (definition.getCardinality() != Cardinality.SINGLE) {
                    throw new IllegalArgumentException("Property '" + id + "' is not a single value property!");
                }
                values = Collections.singletonList(value);
            }

            // assemble property
            PropertyData<?> propertyData = null;
            Object firstValue = (values == null || values.isEmpty() ? null : values.get(0));

            if (definition instanceof PropertyStringDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyStringData(id, (List<String>) null);
                } else if (firstValue instanceof String) {
                    propertyData = bof.createPropertyStringData(id, (List<String>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is a String property!");
                }
            } else if (definition instanceof PropertyIdDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyIdData(id, (List<String>) null);
                } else if (firstValue instanceof String) {
                    propertyData = bof.createPropertyIdData(id, (List<String>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is an Id property!");
                }
            } else if (definition instanceof PropertyHtmlDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyHtmlData(id, (List<String>) values);
                } else if (firstValue instanceof String) {
                    propertyData = bof.createPropertyHtmlData(id, (List<String>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is a HTML property!");
                }
            } else if (definition instanceof PropertyUriDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyUriData(id, (List<String>) null);
                } else if (firstValue instanceof String) {
                    propertyData = bof.createPropertyUriData(id, (List<String>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is an URI property!");
                }
            } else if (definition instanceof PropertyIntegerDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyIntegerData(id, (List<BigInteger>) null);
                } else if (firstValue instanceof BigInteger) {
                    propertyData = bof.createPropertyIntegerData(id, (List<BigInteger>) values);
                } else if ((firstValue instanceof Byte) || (firstValue instanceof Short)
                        || (firstValue instanceof Integer) || (firstValue instanceof Long)) {
                    // we accept all kinds of integers
                    List<BigInteger> list = new ArrayList<BigInteger>(values.size());
                    for (Object v : values) {
                        list.add(BigInteger.valueOf(((Number) v).longValue()));
                    }

                    propertyData = bof.createPropertyIntegerData(id, list);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is an Integer property!");
                }
            } else if (definition instanceof PropertyBooleanDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyBooleanData(id, (List<Boolean>) null);
                } else if (firstValue instanceof Boolean) {
                    propertyData = bof.createPropertyBooleanData(id, (List<Boolean>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is a Boolean property!");
                }
            } else if (definition instanceof PropertyDecimalDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyDecimalData(id, (List<BigDecimal>) null);
                } else if (firstValue instanceof BigDecimal) {
                    propertyData = bof.createPropertyDecimalData(id, (List<BigDecimal>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is a Decimal property!");
                }
            } else if (definition instanceof PropertyDateTimeDefinition) {
                if (firstValue == null) {
                    propertyData = bof.createPropertyDateTimeData(id, (List<GregorianCalendar>) null);
                } else if (firstValue instanceof GregorianCalendar) {
                    propertyData = bof.createPropertyDateTimeData(id, (List<GregorianCalendar>) values);
                } else {
                    throw new IllegalArgumentException("Property '" + id + "' is a Decimal property!");
                }
            }

            // do we have something?
            if (propertyData == null) {
                throw new IllegalArgumentException("Property '" + id + "' doesn't match the property defintion!");
            }

            propertyList.add(propertyData);
        }

        return bof.createPropertiesData(propertyList);
    }

    public List<PropertyData<?>> convertQueryProperties(Properties properties) {
        // check input
        if ((properties == null) || (properties.getProperties() == null)) {
            throw new IllegalArgumentException("Properties must be set!");
        }
        return new ArrayList<PropertyData<?>>(properties.getPropertyList());
    }

    // objects

    public CmisObject convertObject(ObjectData objectData, OperationContext context) {
        if (objectData == null) {
            throw new IllegalArgumentException("Object data is null!");
        }

        ObjectType type = getTypeFromObjectData(objectData);

        /* determine type */
        switch (objectData.getBaseTypeId()) {
        case CMIS_DOCUMENT:
            return new PersistentDocumentImpl(this.session, type, objectData, context);
        case CMIS_FOLDER:
            return new PersistentFolderImpl(this.session, type, objectData, context);
        case CMIS_POLICY:
            return new PersistentPolicyImpl(this.session, type, objectData, context);
        case CMIS_RELATIONSHIP:
            return new PersistentRelationshipImpl(this.session, type, objectData, context);
        default:
            throw new CmisRuntimeException("unsupported type: " + objectData.getBaseTypeId());
        }
    }

    public QueryResult convertQueryResult(ObjectData objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("Object data is null!");
        }

        return new QueryResultImpl(session, objectData);
    }

    public ChangeEvent convertChangeEvent(ObjectData objectData) {
        ChangeType changeType = null;
        GregorianCalendar changeTime = null;
        String objectId = null;
        Map<String, List<?>> properties = null;
        List<String> policyIds = null;
        Acl acl = null;

        if (objectData.getChangeEventInfo() != null) {
            changeType = objectData.getChangeEventInfo().getChangeType();
            changeTime = objectData.getChangeEventInfo().getChangeTime();
        }

        if ((objectData.getProperties() != null) && (objectData.getProperties().getPropertyList() != null)) {
            properties = new HashMap<String, List<?>>();

            for (PropertyData<?> property : objectData.getProperties().getPropertyList()) {
                properties.put(property.getId(), property.getValues());
            }

            if (properties.containsKey(PropertyIds.OBJECT_ID)) {
                List<?> objectIdList = properties.get(PropertyIds.OBJECT_ID);
                if ((objectIdList != null) && (!objectIdList.isEmpty()))
                    objectId = objectIdList.get(0).toString();
            }

            if ((objectData.getPolicyIds() != null) && (objectData.getPolicyIds().getPolicyIds() != null)) {
                policyIds = objectData.getPolicyIds().getPolicyIds();
            }

            if (objectData.getAcl() != null) {
                acl = objectData.getAcl();
            }
        }

        return new ChangeEventImpl(changeType, changeTime, objectId, properties, policyIds, acl);
    }

    public ChangeEvents convertChangeEvents(String changeLogToken, ObjectList objectList) {
        if (objectList == null) {
            return null;
        }

        List<ChangeEvent> events = new ArrayList<ChangeEvent>();
        if (objectList.getObjects() != null) {
            for (ObjectData objectData : objectList.getObjects()) {
                if (objectData == null) {
                    continue;
                }

                events.add(convertChangeEvent(objectData));
            }
        }

        boolean hasMoreItems = (objectList.hasMoreItems() == null ? false : objectList.hasMoreItems().booleanValue());
        long totalNumItems = (objectList.getNumItems() == null ? -1 : objectList.getNumItems().longValue());

        return new ChangeEventsImpl(changeLogToken, events, hasMoreItems, totalNumItems);
    }

}
