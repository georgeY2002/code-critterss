/*-
 * #%L
 * Code Critters
 * %%
 * Copyright (C) 2019 Michael Gruber
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import {html, PolymerElement} from '/lib/@polymer/polymer/polymer-element.js';
import { afterNextRender } from '/lib/@polymer/polymer/lib/utils/render-status.js';
import {I18n} from '../critter-i18n/critter-i18n-mixin.js';

import '../critter-form/critter-form.js';
import '../critter-button/critter-button.js';
import '../critter-input/critter-input.js';





/*
# critter-insert

A Simple Button

## Example
```html
<critter-reset-password></critter-reset-password>
```

@demo
*/

class CritterResetPassword extends I18n(PolymerElement) {
    static get template() {
        return html`
            <style>
            .controls critter-button{
                margin: auto;
           }
            </style>
            
            <critter-form id="form" method="POST" target="[[target]]" >
                <critter-input label="password" name="password" type="password"></critter-input><br>
                <critter-input label="password_confirm" name="password" type="password" no-serialize match="password"></critter-input><br>
                <div class="controls" slot="controls">
                    <critter-button submit>[[__("reset")]]</critter-button>
                </div>
            </critter-form>
        `;
    }

    static get is() {
        return 'critter-reset-password';
    }

    static get properties() {
        return {
            target: {
                type: String
            }
        }
    }

    connectedCallback() {
        super.connectedCallback();

        afterNextRender(this, function () {
            let secret = new URL(window.location.href).searchParams.get("secret");
            if(!secret){
                window.location.replace("/?badSecret=true");
            }
            this.target = "/users/reset/" + new URL(window.location.href).searchParams.get("secret");
            this.$.form.onSuccess = this._onSuccess;
            this.$.form.onError = this._onError;
        });
    }

    _onSuccess() {
        window.location.replace("/?resetSuccess=true");
    }

    _onError() {
        this.showErrorToast("bad_request")
        window.location.replace("/?resetSuccess=false");
    }

}

window.customElements.define(CritterResetPassword.is, CritterResetPassword);
