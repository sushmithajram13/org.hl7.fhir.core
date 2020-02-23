package org.hl7.fhir.android.generated.dstu2;

/*-
 * #%L
 * org.hl7.fhir.dstu2
 * %%
 * Copyright (C) 2014 - 2019 Health Level 7
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/*
  Copyright (c) 2011+, HL7, Inc.
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without modification, 
  are permitted provided that the following conditions are met:
  
   * Redistributions of source code must retain the above copyright notice, this 
     list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, 
     this list of conditions and the following disclaimer in the documentation 
     and/or other materials provided with the distribution.
   * Neither the name of HL7 nor the names of its contributors may be used to 
     endorse or promote products derived from this software without specific 
     prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
  
*/
// Generated on Wed, Jul 13, 2016 05:32+1000 for FHIR v1.0.2
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.android.generated.dstu2.OrderResponseEnum.OrderStatus;
import org.hl7.fhir.android.generated.dstu2.OrderResponseEnum.OrderStatusEnumFactory;

/**
 * A response to an order.
 */
@ResourceDef(name = "OrderResponse", profile = "http://hl7.org/fhir/Profile/OrderResponse")
public class OrderResponse extends DomainResource {

    /**
     * Identifiers assigned to this order. The identifiers are usually assigned by the system responding to the order, but they may be provided or added to by other systems.
     */
    @Child(name = "identifier", type = { Identifier.class }, order = 0, min = 0, max = Child.MAX_UNLIMITED, modifier = false, summary = true)
    @Description(shortDefinition = "Identifiers assigned to this order by the orderer or by the receiver", formalDefinition = "Identifiers assigned to this order. The identifiers are usually assigned by the system responding to the order, but they may be provided or added to by other systems.")
    protected List<Identifier> identifier;

    /**
     * A reference to the order that this is in response to.
     */
    @Child(name = "request", type = { Order.class }, order = 1, min = 1, max = 1, modifier = false, summary = true)
    @Description(shortDefinition = "The order that this is a response to", formalDefinition = "A reference to the order that this is in response to.")
    protected Reference request;

    /**
     * The actual object that is the target of the reference (A reference to the order that this is in response to.)
     */
    protected Order requestTarget;

    /**
     * The date and time at which this order response was made (created/posted).
     */
    @Child(name = "date", type = { DateTimeType.class }, order = 2, min = 0, max = 1, modifier = false, summary = true)
    @Description(shortDefinition = "When the response was made", formalDefinition = "The date and time at which this order response was made (created/posted).")
    protected DateTimeType date;

    /**
     * The person, organization, or device credited with making the response.
     */
    @Child(name = "who", type = { Practitioner.class, Organization.class, Device.class }, order = 3, min = 0, max = 1, modifier = false, summary = true)
    @Description(shortDefinition = "Who made the response", formalDefinition = "The person, organization, or device credited with making the response.")
    protected Reference who;

    /**
     * The actual object that is the target of the reference (The person, organization, or device credited with making the response.)
     */
    protected Resource whoTarget;

    /**
     * What this response says about the status of the original order.
     */
    @Child(name = "orderStatus", type = { CodeType.class }, order = 4, min = 1, max = 1, modifier = true, summary = true)
    @Description(shortDefinition = "pending | review | rejected | error | accepted | cancelled | replaced | aborted | completed", formalDefinition = "What this response says about the status of the original order.")
    protected Enumeration<OrderStatus> orderStatus;

    /**
     * Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.
     */
    @Child(name = "description", type = { StringType.class }, order = 5, min = 0, max = 1, modifier = false, summary = true)
    @Description(shortDefinition = "Additional description of the response", formalDefinition = "Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.")
    protected StringType description;

    /**
     * Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.
     */
    @Child(name = "fulfillment", type = {}, order = 6, min = 0, max = Child.MAX_UNLIMITED, modifier = false, summary = true)
    @Description(shortDefinition = "Details of the outcome of performing the order", formalDefinition = "Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.")
    protected List<Reference> fulfillment;

    /**
     * The actual objects that are the target of the reference (Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.)
     */
    protected List<Resource> fulfillmentTarget;

    private static final long serialVersionUID = -856633109L;

    /*
   * Constructor
   */
    public OrderResponse() {
        super();
    }

    /*
   * Constructor
   */
    public OrderResponse(Reference request, Enumeration<OrderStatus> orderStatus) {
        super();
        this.request = request;
        this.orderStatus = orderStatus;
    }

    /**
     * @return {@link #identifier} (Identifiers assigned to this order. The identifiers are usually assigned by the system responding to the order, but they may be provided or added to by other systems.)
     */
    public List<Identifier> getIdentifier() {
        if (this.identifier == null)
            this.identifier = new ArrayList<Identifier>();
        return this.identifier;
    }

    public boolean hasIdentifier() {
        if (this.identifier == null)
            return false;
        for (Identifier item : this.identifier) if (!item.isEmpty())
            return true;
        return false;
    }

    /**
     * @return {@link #identifier} (Identifiers assigned to this order. The identifiers are usually assigned by the system responding to the order, but they may be provided or added to by other systems.)
     */
    // syntactic sugar
    public Identifier addIdentifier() {
        // 3
        Identifier t = new Identifier();
        if (this.identifier == null)
            this.identifier = new ArrayList<Identifier>();
        this.identifier.add(t);
        return t;
    }

    // syntactic sugar
    public OrderResponse addIdentifier(Identifier t) {
        // 3
        if (t == null)
            return this;
        if (this.identifier == null)
            this.identifier = new ArrayList<Identifier>();
        this.identifier.add(t);
        return this;
    }

    /**
     * @return {@link #request} (A reference to the order that this is in response to.)
     */
    public Reference getRequest() {
        if (this.request == null)
            if (Configuration.errorOnAutoCreate())
                throw new Error("Attempt to auto-create OrderResponse.request");
            else if (Configuration.doAutoCreate())
                // cc
                this.request = new Reference();
        return this.request;
    }

    public boolean hasRequest() {
        return this.request != null && !this.request.isEmpty();
    }

    /**
     * @param value {@link #request} (A reference to the order that this is in response to.)
     */
    public OrderResponse setRequest(Reference value) {
        this.request = value;
        return this;
    }

    /**
     * @return {@link #request} The actual object that is the target of the reference. The reference library doesn't populate this, but you can use it to hold the resource if you resolve it. (A reference to the order that this is in response to.)
     */
    public Order getRequestTarget() {
        if (this.requestTarget == null)
            if (Configuration.errorOnAutoCreate())
                throw new Error("Attempt to auto-create OrderResponse.request");
            else if (Configuration.doAutoCreate())
                // aa
                this.requestTarget = new Order();
        return this.requestTarget;
    }

    /**
     * @param value {@link #request} The actual object that is the target of the reference. The reference library doesn't use these, but you can use it to hold the resource if you resolve it. (A reference to the order that this is in response to.)
     */
    public OrderResponse setRequestTarget(Order value) {
        this.requestTarget = value;
        return this;
    }

    /**
     * @return {@link #date} (The date and time at which this order response was made (created/posted).). This is the underlying object with id, value and extensions. The accessor "getDate" gives direct access to the value
     */
    public DateTimeType getDateElement() {
        if (this.date == null)
            if (Configuration.errorOnAutoCreate())
                throw new Error("Attempt to auto-create OrderResponse.date");
            else if (Configuration.doAutoCreate())
                // bb
                this.date = new DateTimeType();
        return this.date;
    }

    public boolean hasDateElement() {
        return this.date != null && !this.date.isEmpty();
    }

    public boolean hasDate() {
        return this.date != null && !this.date.isEmpty();
    }

    /**
     * @param value {@link #date} (The date and time at which this order response was made (created/posted).). This is the underlying object with id, value and extensions. The accessor "getDate" gives direct access to the value
     */
    public OrderResponse setDateElement(DateTimeType value) {
        this.date = value;
        return this;
    }

    /**
     * @return The date and time at which this order response was made (created/posted).
     */
    public Date getDate() {
        return this.date == null ? null : this.date.getValue();
    }

    /**
     * @param value The date and time at which this order response was made (created/posted).
     */
    public OrderResponse setDate(Date value) {
        if (value == null)
            this.date = null;
        else {
            if (this.date == null)
                this.date = new DateTimeType();
            this.date.setValue(value);
        }
        return this;
    }

    /**
     * @return {@link #who} (The person, organization, or device credited with making the response.)
     */
    public Reference getWho() {
        if (this.who == null)
            if (Configuration.errorOnAutoCreate())
                throw new Error("Attempt to auto-create OrderResponse.who");
            else if (Configuration.doAutoCreate())
                // cc
                this.who = new Reference();
        return this.who;
    }

    public boolean hasWho() {
        return this.who != null && !this.who.isEmpty();
    }

    /**
     * @param value {@link #who} (The person, organization, or device credited with making the response.)
     */
    public OrderResponse setWho(Reference value) {
        this.who = value;
        return this;
    }

    /**
     * @return {@link #who} The actual object that is the target of the reference. The reference library doesn't populate this, but you can use it to hold the resource if you resolve it. (The person, organization, or device credited with making the response.)
     */
    public Resource getWhoTarget() {
        return this.whoTarget;
    }

    /**
     * @param value {@link #who} The actual object that is the target of the reference. The reference library doesn't use these, but you can use it to hold the resource if you resolve it. (The person, organization, or device credited with making the response.)
     */
    public OrderResponse setWhoTarget(Resource value) {
        this.whoTarget = value;
        return this;
    }

    /**
     * @return {@link #orderStatus} (What this response says about the status of the original order.). This is the underlying object with id, value and extensions. The accessor "getOrderStatus" gives direct access to the value
     */
    public Enumeration<OrderStatus> getOrderStatusElement() {
        if (this.orderStatus == null)
            if (Configuration.errorOnAutoCreate())
                throw new Error("Attempt to auto-create OrderResponse.orderStatus");
            else if (Configuration.doAutoCreate())
                // bb
                this.orderStatus = new Enumeration<OrderStatus>(new OrderStatusEnumFactory());
        return this.orderStatus;
    }

    public boolean hasOrderStatusElement() {
        return this.orderStatus != null && !this.orderStatus.isEmpty();
    }

    public boolean hasOrderStatus() {
        return this.orderStatus != null && !this.orderStatus.isEmpty();
    }

    /**
     * @param value {@link #orderStatus} (What this response says about the status of the original order.). This is the underlying object with id, value and extensions. The accessor "getOrderStatus" gives direct access to the value
     */
    public OrderResponse setOrderStatusElement(Enumeration<OrderStatus> value) {
        this.orderStatus = value;
        return this;
    }

    /**
     * @return What this response says about the status of the original order.
     */
    public OrderStatus getOrderStatus() {
        return this.orderStatus == null ? null : this.orderStatus.getValue();
    }

    /**
     * @param value What this response says about the status of the original order.
     */
    public OrderResponse setOrderStatus(OrderStatus value) {
        if (this.orderStatus == null)
            this.orderStatus = new Enumeration<OrderStatus>(new OrderStatusEnumFactory());
        this.orderStatus.setValue(value);
        return this;
    }

    /**
     * @return {@link #description} (Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.). This is the underlying object with id, value and extensions. The accessor "getDescription" gives direct access to the value
     */
    public StringType getDescriptionElement() {
        if (this.description == null)
            if (Configuration.errorOnAutoCreate())
                throw new Error("Attempt to auto-create OrderResponse.description");
            else if (Configuration.doAutoCreate())
                // bb
                this.description = new StringType();
        return this.description;
    }

    public boolean hasDescriptionElement() {
        return this.description != null && !this.description.isEmpty();
    }

    public boolean hasDescription() {
        return this.description != null && !this.description.isEmpty();
    }

    /**
     * @param value {@link #description} (Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.). This is the underlying object with id, value and extensions. The accessor "getDescription" gives direct access to the value
     */
    public OrderResponse setDescriptionElement(StringType value) {
        this.description = value;
        return this;
    }

    /**
     * @return Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.
     */
    public String getDescription() {
        return this.description == null ? null : this.description.getValue();
    }

    /**
     * @param value Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.
     */
    public OrderResponse setDescription(String value) {
        if (Utilities.noString(value))
            this.description = null;
        else {
            if (this.description == null)
                this.description = new StringType();
            this.description.setValue(value);
        }
        return this;
    }

    /**
     * @return {@link #fulfillment} (Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.)
     */
    public List<Reference> getFulfillment() {
        if (this.fulfillment == null)
            this.fulfillment = new ArrayList<Reference>();
        return this.fulfillment;
    }

    public boolean hasFulfillment() {
        if (this.fulfillment == null)
            return false;
        for (Reference item : this.fulfillment) if (!item.isEmpty())
            return true;
        return false;
    }

    /**
     * @return {@link #fulfillment} (Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.)
     */
    // syntactic sugar
    public Reference addFulfillment() {
        // 3
        Reference t = new Reference();
        if (this.fulfillment == null)
            this.fulfillment = new ArrayList<Reference>();
        this.fulfillment.add(t);
        return t;
    }

    // syntactic sugar
    public OrderResponse addFulfillment(Reference t) {
        // 3
        if (t == null)
            return this;
        if (this.fulfillment == null)
            this.fulfillment = new ArrayList<Reference>();
        this.fulfillment.add(t);
        return this;
    }

    /**
     * @return {@link #fulfillment} (The actual objects that are the target of the reference. The reference library doesn't populate this, but you can use this to hold the resources if you resolvethemt. Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.)
     */
    public List<Resource> getFulfillmentTarget() {
        if (this.fulfillmentTarget == null)
            this.fulfillmentTarget = new ArrayList<Resource>();
        return this.fulfillmentTarget;
    }

    protected void listChildren(List<Property> childrenList) {
        super.listChildren(childrenList);
        childrenList.add(new Property("identifier", "Identifier", "Identifiers assigned to this order. The identifiers are usually assigned by the system responding to the order, but they may be provided or added to by other systems.", 0, java.lang.Integer.MAX_VALUE, identifier));
        childrenList.add(new Property("request", "Reference(Order)", "A reference to the order that this is in response to.", 0, java.lang.Integer.MAX_VALUE, request));
        childrenList.add(new Property("date", "dateTime", "The date and time at which this order response was made (created/posted).", 0, java.lang.Integer.MAX_VALUE, date));
        childrenList.add(new Property("who", "Reference(Practitioner|Organization|Device)", "The person, organization, or device credited with making the response.", 0, java.lang.Integer.MAX_VALUE, who));
        childrenList.add(new Property("orderStatus", "code", "What this response says about the status of the original order.", 0, java.lang.Integer.MAX_VALUE, orderStatus));
        childrenList.add(new Property("description", "string", "Additional description about the response - e.g. a text description provided by a human user when making decisions about the order.", 0, java.lang.Integer.MAX_VALUE, description));
        childrenList.add(new Property("fulfillment", "Reference(Any)", "Links to resources that provide details of the outcome of performing the order; e.g. Diagnostic Reports in a response that is made to an order that referenced a diagnostic order.", 0, java.lang.Integer.MAX_VALUE, fulfillment));
    }

    @Override
    public void setProperty(String name, Base value) throws FHIRException {
        if (name.equals("identifier"))
            this.getIdentifier().add(castToIdentifier(value));
        else if (name.equals("request"))
            // Reference
            this.request = castToReference(value);
        else if (name.equals("date"))
            // DateTimeType
            this.date = castToDateTime(value);
        else if (name.equals("who"))
            // Reference
            this.who = castToReference(value);
        else if (name.equals("orderStatus"))
            // Enumeration<OrderStatus>
            this.orderStatus = new OrderStatusEnumFactory().fromType(value);
        else if (name.equals("description"))
            // StringType
            this.description = castToString(value);
        else if (name.equals("fulfillment"))
            this.getFulfillment().add(castToReference(value));
        else
            super.setProperty(name, value);
    }

    @Override
    public Base addChild(String name) throws FHIRException {
        if (name.equals("identifier")) {
            return addIdentifier();
        } else if (name.equals("request")) {
            this.request = new Reference();
            return this.request;
        } else if (name.equals("date")) {
            throw new FHIRException("Cannot call addChild on a primitive type OrderResponse.date");
        } else if (name.equals("who")) {
            this.who = new Reference();
            return this.who;
        } else if (name.equals("orderStatus")) {
            throw new FHIRException("Cannot call addChild on a primitive type OrderResponse.orderStatus");
        } else if (name.equals("description")) {
            throw new FHIRException("Cannot call addChild on a primitive type OrderResponse.description");
        } else if (name.equals("fulfillment")) {
            return addFulfillment();
        } else
            return super.addChild(name);
    }

    public String fhirType() {
        return "OrderResponse";
    }

    public OrderResponse copy() {
        OrderResponse dst = new OrderResponse();
        copyValues(dst);
        if (identifier != null) {
            dst.identifier = new ArrayList<Identifier>();
            for (Identifier i : identifier) dst.identifier.add(i.copy());
        }
        ;
        dst.request = request == null ? null : request.copy();
        dst.date = date == null ? null : date.copy();
        dst.who = who == null ? null : who.copy();
        dst.orderStatus = orderStatus == null ? null : orderStatus.copy();
        dst.description = description == null ? null : description.copy();
        if (fulfillment != null) {
            dst.fulfillment = new ArrayList<Reference>();
            for (Reference i : fulfillment) dst.fulfillment.add(i.copy());
        }
        ;
        return dst;
    }

    protected OrderResponse typedCopy() {
        return copy();
    }

    @Override
    public boolean equalsDeep(Base other) {
        if (!super.equalsDeep(other))
            return false;
        if (!(other instanceof OrderResponse))
            return false;
        OrderResponse o = (OrderResponse) other;
        return compareDeep(identifier, o.identifier, true) && compareDeep(request, o.request, true) && compareDeep(date, o.date, true) && compareDeep(who, o.who, true) && compareDeep(orderStatus, o.orderStatus, true) && compareDeep(description, o.description, true) && compareDeep(fulfillment, o.fulfillment, true);
    }

    @Override
    public boolean equalsShallow(Base other) {
        if (!super.equalsShallow(other))
            return false;
        if (!(other instanceof OrderResponse))
            return false;
        OrderResponse o = (OrderResponse) other;
        return compareValues(date, o.date, true) && compareValues(orderStatus, o.orderStatus, true) && compareValues(description, o.description, true);
    }

    public boolean isEmpty() {
        return super.isEmpty() && (identifier == null || identifier.isEmpty()) && (request == null || request.isEmpty()) && (date == null || date.isEmpty()) && (who == null || who.isEmpty()) && (orderStatus == null || orderStatus.isEmpty()) && (description == null || description.isEmpty()) && (fulfillment == null || fulfillment.isEmpty());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.OrderResponse;
    }

    @SearchParamDefinition(name = "date", path = "OrderResponse.date", description = "When the response was made", type = "date")
    public static final String SP_DATE = "date";

    @SearchParamDefinition(name = "request", path = "OrderResponse.request", description = "The order that this is a response to", type = "reference")
    public static final String SP_REQUEST = "request";

    @SearchParamDefinition(name = "identifier", path = "OrderResponse.identifier", description = "Identifiers assigned to this order by the orderer or by the receiver", type = "token")
    public static final String SP_IDENTIFIER = "identifier";

    @SearchParamDefinition(name = "code", path = "OrderResponse.orderStatus", description = "pending | review | rejected | error | accepted | cancelled | replaced | aborted | completed", type = "token")
    public static final String SP_CODE = "code";

    @SearchParamDefinition(name = "fulfillment", path = "OrderResponse.fulfillment", description = "Details of the outcome of performing the order", type = "reference")
    public static final String SP_FULFILLMENT = "fulfillment";

    @SearchParamDefinition(name = "who", path = "OrderResponse.who", description = "Who made the response", type = "reference")
    public static final String SP_WHO = "who";
}