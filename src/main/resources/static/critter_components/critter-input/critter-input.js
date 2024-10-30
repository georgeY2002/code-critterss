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
import {I18n} from '../critter-i18n/critter-i18n-mixin.js';


/*
# critter-insert

A Simple Button

## Example
```html
<critter-insert>text</critter-insert>
```

@demo
*/

class CritterInput extends I18n(PolymerElement) {
    static get template() {
        return html`
            <style>
                               
                *{
                    height: 30px;
                }
                
                :host {
                     --display-field: table-row;
                     display: var(--display-field);
                }
        
                span{
                    cursor: default;
                    text-align: center;
                    justify-content: center;
                    align-items: center;
                    display: table-cell;
                }
        
                input{
                    margin-left: 10px;
                    padding: 0 5px;
                    font-size: 1em;
                    background-color: transparent;
                    border: none;
                    border-bottom: 2px solid #FFA600;
                    display: table-cell;
                }
        
                input:focus{
                    border: none;
                    border-bottom: 2px solid #FFA600;
                    outline: none;
                }
        
                input:invalid{
                    border: none;
                    border-bottom: 2px solid darkred;
                    outline: none;
                }
            </style>
            
            <span>[[__(label)]]</span>
            <input id="field" type="[[type]]" value="{{value::input}}" name="[[name]]" placeholder$="{{placeholder}}">
        `;
    }

    static get is() {
        return 'critter-input';
    }

    static get properties() {
        return {
            value: {
                type: String,
                observer: "_onValueChange",
                notify: true
            },

            name: {
                type: String,
                value: ""
            },

            type: {
                type: String,
                value: "text",
                observer: "_onTypeChange"
            },

            placeholder: {
                type: String
            },

            label: {
                type: String
            },

            valid: {
                type: Boolean,
                value: true,
                observer: "_onValidChange"
            },

            noSerialize: {
                type: Boolean
            },

            match: {
                type: String,
                value: ""
            },
        }
    }

    _onValueChange() {
        this.dispatchEvent(new CustomEvent('valueChanged', {
            detail: {name: this.value},
            bubbles: true,
            composed: true
        }));
    }

    _onValidChange() {
        this.$.field.setCustomValidity(this.valid ? "" : "name already exists");
    }

    _onTypeChange(){
        if(this.type === "hidden") {
            this.updateStyles({
                '--display-field': 'none'
            });
        } else {
            this.updateStyles({
                '--display-field': 'table-row'
            });
        }
    }

    serialize() {
        if(!this.noSerialize) {
            let data = {};
            data[this.name] = this.value;
            return data;
        }
    }
}

window.customElements.define(CritterInput.is, CritterInput);
