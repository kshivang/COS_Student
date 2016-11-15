package org.invincible.cosstudent.wizard.model;

/**
 * Callback interface connecting {@link Page}, {@link AbstractWizardModel}, and model container
 */
public interface ModelCallbacks {
    void onPageDataChanged(Page page);
    void onPageTreeChanged();
}
