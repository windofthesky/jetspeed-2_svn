package org.apache.jetspeed.om.common.preference;

import java.io.Serializable;

import javax.portlet.PreferencesValidator;

public interface PreferencesValidatorFactory extends Serializable
{
    PreferencesValidator getPreferencesValidator();
}
