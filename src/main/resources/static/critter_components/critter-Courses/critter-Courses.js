/*-
 * #%L
 * Code Critters
 * %%
 * Copyright (C) 2019 - 2024 Michael Gruber
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
import { html, PolymerElement } from '/lib/@polymer/polymer/polymer-element.js';

class CourseSections extends PolymerElement {
    static get template() {
        return html`
        <style>
            .section-header {
                font-size: 24px;
                margin: 20px 0;
                font-weight: bold;
                color: #5C33F6;
            }

            .section {
                margin-bottom: 30px;
            }

            .section a {
                display: block;
                margin: 10px 0;
                color: #5C33F6;
                text-decoration: none;
                font-size: 18px;
                cursor: pointer;
            }

            .section a:hover {
                color: #FF3C69;
                text-decoration: underline;
            }
        </style>

        <div>
            <div class="section-header">{{localize('header')}}</div>

            <div class="section">
                <h3>{{localize('basic_phase')}}</h3>
                <a href="/course/html-intro">{{localize('basic1')}}</a>
                <a href="/course/html-elements">{{localize('basic2')}}</a>
            </div>

            <div class="section">
                <h3>{{localize('medium_phase')}}</h3>
                <a href="/course/forms-inputs">{{localize('medium1')}}</a>
                <a href="/course/html-tables">{{localize('medium2')}}</a>
                <a href="/course/html-media">{{localize('medium3')}}</a>
            </div>

            <div class="section">
                <h3>{{localize('hard_phase')}}</h3>
                <a href="/course/semantic-html">{{localize('hard1')}}</a>
                <a href="/course/advanced-forms">{{localize('hard2')}}</a>
            </div>

            <div class="section">
                <h3>{{localize('quiz_section')}}</h3>
                <a href="/course/quiz1">{{localize('quiz1')}}</a>
                <a href="/course/quiz2">{{localize('quiz2')}}</a>
                <a href="/course/quiz3">{{localize('quiz3')}}</a>
            </div>
        </div>
        `;
    }

    static get properties() {
        return {
            language: {
                type: String,
                value: 'en'  // Default to English
            }
        }
    }

    localize(key) {
        return window.Core.dictionary[key] || key;
    }
}

window.customElements.define('course-sections', CourseSections);
