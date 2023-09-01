/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.selectbooleanbutton;

import java.io.IOException;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.*;

public class SelectBooleanButtonRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectBooleanButton button = (SelectBooleanButton) component;

        if (!shouldDecode(button)) {
            return;
        }

        decodeBehaviors(context, button);

        String clientId = button.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        boolean checked = "on".equalsIgnoreCase(submittedValue);
        button.setSubmittedValue(checked);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectBooleanButton button = (SelectBooleanButton) component;

        calculateLabels(context, button);
        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    /**
     * Determine if not iconOnly and no labels provided set the default labels.
     */
    private void calculateLabels(FacesContext context, SelectBooleanButton button) {
        boolean hasLabel = LangUtils.isNotBlank(button.getOnLabel()) || LangUtils.isNotBlank(button.getOffLabel());
        if (hasLabel) {
            return;
        }
        boolean hasIcon = LangUtils.isNotBlank(button.getOnIcon()) || LangUtils.isNotBlank(button.getOffIcon());
        if (!hasIcon) {
            // no icon or label use defaults
            button.setOnLabel(MessageFactory.getMessage(SelectBooleanButtonBase.LABEL_ON));
            button.setOffLabel(MessageFactory.getMessage(SelectBooleanButtonBase.LABEL_OFF));
        }
    }

    protected void encodeMarkup(FacesContext context, SelectBooleanButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        boolean checked = Boolean.parseBoolean(ComponentUtils.getValueToRender(context, button));
        boolean disabled = button.isDisabled();
        String inputId = clientId + "_input";
        String label = checked ? button.getOnLabel() : button.getOffLabel();
        String icon = checked ? button.getOnIcon() : button.getOffIcon();
        boolean hasIcon = icon != null;
        String title = button.getTitle();
        String style = button.getStyle();
        String styleClass = "ui-selectbooleanbutton " + button.resolveStyleClass(checked, disabled);

        // button
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        // input
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute(HTML.ARIA_LABEL, label, null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderValidationMetadata(context, button);
        renderAccessibilityAttributes(context, button);
        renderPassThruAttributes(context, button, HTML.TAB_INDEX);
        renderOnchange(context, button);
        renderDomEvents(context, button, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");

        // icon
        if (hasIcon) {
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }

        // label
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        renderButtonValue(writer, true, label, button.getTitle(), button.getAriaLabel());

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectBooleanButton button) throws IOException {

        String onLabel = button.getOnLabel();
        String offLabel = button.getOffLabel();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectBooleanButton", button)
                    .attr("onLabel", isValueBlank(onLabel) ? "ui-button" : onLabel)
                    .attr("offLabel", isValueBlank(offLabel) ? "ui-button" : offLabel)
                    .attr("onIcon", button.getOnIcon(), null)
                    .attr("offIcon", button.getOffIcon(), null);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        if (submittedValue instanceof Boolean) {
            return submittedValue;
        }
        Object convertedValue = ComponentUtils.getConvertedValue(context, component, submittedValue);
        return ((convertedValue instanceof Boolean) ? convertedValue : Boolean.valueOf(Objects.toString(convertedValue)));
    }

    @Override
    protected String getHighlighter() {
        return "booleanbutton";
    }
}
