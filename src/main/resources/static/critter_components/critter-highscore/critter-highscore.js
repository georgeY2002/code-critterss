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
import {afterNextRender} from '/lib/@polymer/polymer/lib/utils/render-status.js';
import {I18n} from '../critter-i18n/critter-i18n-mixin.js';

import '/lib/@polymer/iron-ajax/iron-ajax.js';

import '../critter-data-store/critter-data-store.js';


/*
# critter-insert

A Simple Button

## Example
```html
<critter-insert>text</critter-insert>
```

@demo
*/

class CritterHighscore extends I18n(PolymerElement) {
    static get template() {
        return html`
            <style>
            
            :host {
                width: 100%;
                height: 0;
            }
            
            .table{
                display: table;
                width: 100%;
                border-collapse: collapse;
                margin: auto;
            }
            
            .row{
                display: table-row;
                font-size: 1.3em;
            }
            
            .cell {
                display: table-cell;
                text-align: center;
            }
            
            .heading_row{
                border-bottom: solid 3px #FFA600;
                font-size: 1.5em;
            }
            
            .myResult{
                background-color: lightgoldenrodyellow;
            }
            
            .space{
                height: 20px;
            }
         
            </style>
            <critter-data-store></critter-data-store>
            
            <div class="table">
                <div class="row heading_row">
                    <div class="cell heading_cell">{{__("position")}}</div>
                    <div class="cell heading_cell">{{__("username")}}</div>
                    <div class="cell heading_cell">{{__("played_levels")}}</div>
                    <div class="cell heading_cell">{{__("score")}}</div>
                </div>
                <template is="dom-repeat" items="{{highscore}}">
                    <div class$="[[_getRowClass(item, myScore)]]">
                        <div class="cell">{{item.position}}</div>
                        <div class="cell">{{item.user}}</div>
                        <div class="cell">{{item.levels}}</div>
                        <div class="cell">{{item.score}}</div>
                    </div>
                </template>
                
                <template is="dom-if" if="{{!_computeInHighscore(myScore)}}">
                    <div class="space row"></div>
                    <div class="row myResult">
                        <div class="cell">{{myScore.position}}</div>
                        <div class="cell">{{myScore.user}}</div>
                        <div class="cell">{{myScore.levels}}</div>
                        <div class="cell">{{myScore.score}}</div>
                    </div>
                </template>
            </div>
            
          
        `;
    }

    static get is() {
        return 'critter-highscore';
    }

    static get properties() {
        return {
            highscore: {
                type: Array,
                value: []
            },

            myScore: {
                type: Object,
                value: {}
            }
        }
    }

    connectedCallback() {
        super.connectedCallback();
        this.getHighscoreData();

        this._globalData = window.Core.CritterLevelData;

        afterNextRender(this, function () {
            this._globalData.addEventListener("_userChanged", this._userChanged.bind(this));
        });
    }

    getHighscoreData() {
        let req = document.createElement('iron-ajax');
        req.url = "/highscore/data";
        req.method = "GET";
        req.handleAs = 'json';
        req.contentType = 'application/json';
        req.bubbles = true;
        req.rejectWithRequest = true;

        req.addEventListener('response', e => {
            let data = e.detail.__data.response;
            this.highscore = data;
        });

        let genRequest = req.generateRequest();
        req.completes = genRequest.completes;
    }

    _userChanged() {
        if (this._globalData.user !== undefined){
            let req = document.createElement('iron-ajax');
            req.url = "/highscore/me";
            req.method = "GET";
            req.handleAs = 'json';
            req.contentType = 'application/json';
            req.bubbles = true;
            req.rejectWithRequest = true;

            req.addEventListener('response', e => {
                let data = e.detail.__data.response;
                this.myScore = data;
            });

            let genRequest = req.generateRequest();
            req.completes = genRequest.completes;
        } else {
            this.myScore = undefined;
        }
    }

    _computeInHighscore(myScore) {
        if(!myScore){
            return true;
        }
        let flag = false;
        this.highscore.forEach((score) => {
            if (score.user === myScore.user) {
                flag = true;
            }
        });
        return flag;
    }

    _getRowClass(item, myScore){
        return (myScore && item.user === myScore.user) ? 'row myResult' : 'row';
    }
}

window.customElements.define(CritterHighscore.is, CritterHighscore);
