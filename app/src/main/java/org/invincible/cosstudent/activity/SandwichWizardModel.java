/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.invincible.cosstudent.activity;

import android.content.Context;

import org.invincible.cosstudent.wizard.model.AbstractWizardModel;
import org.invincible.cosstudent.wizard.model.BranchPage;
import org.invincible.cosstudent.wizard.model.CustomerInfoPage;
import org.invincible.cosstudent.wizard.model.MultipleFixedChoicePage;
import org.invincible.cosstudent.wizard.model.PageList;


public class SandwichWizardModel extends AbstractWizardModel {
    public SandwichWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new BranchPage(this, "MenuFragment")
                        .addBranch("Snack", new MultipleFixedChoicePage(this, "Burger")
                        .setChoices("Sandwich  -  Rs.20", "Burger  -  Rs.25", "Pizza  -  Rs.75"))
                        .addBranch("Main Course", new MultipleFixedChoicePage(this, "Main Course")
                        .setChoices("Biryani  -  Rs.100", "Paneer Pasanda  -  Rs.45")),
                new CustomerInfoPage(this, "Your info")
                        .setRequired(true)
        );
    }
}
