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
class SidebarComponent extends HTMLElement {
    constructor() {
        super();

        // Attach a shadow DOM to the element
        const shadow = this.attachShadow({ mode: 'open' });

        // Create a wrapper for the sidebar
        const wrapper = document.createElement('div');
        wrapper.innerHTML = `
            <style>
                @import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css');

                /* Sidebar Styles */
                .sidebar {
                    position: fixed;
                    top: 0;
                    left: 0;
                    height: 100%;
                    width: 205px;
                    background-color: #ffffff; /* White background */
                    color: #000000; /* Black text */
                    padding: 20px;
                    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                }

                .sidebar img {
                    width: 80%;
                    border-radius: 50%;
                    border: 3px solid #ffa600; /* Orange accent color */
                    margin-bottom: 20px;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    cursor: pointer;
                }

                .sidebar h6 {
                    color: #000000; /* Black text */
                    margin-bottom: 10px;
                    font-size: 1.1em;
                }

                .sidebar a {
                    display: flex;
                    align-items: center;
                    color: #000000; /* Black text */
                    padding: 23px 10px; /* Reduced padding to make background smaller */
                    width: 100%;
                    text-decoration: none;
                    font-size: 1.1em;
                    border-radius: 8px;
                    transition: background-color 0.3s ease, color 0.3s ease;
                }

                .sidebar a:hover {
                    background-color: #ffa600; /* Orange hover background */
                    color: #ffffff; /* White text on hover */
                }

                .sidebar i {
                    margin-right: 10px;
                    font-size: 1.2em; /* Ensure the icon size is aligned */
                }

                .sidebar h6 {
                    display: inline; /* Align text and icon vertically */
                    margin: 0;
                }

                .sidebar .active {
                    background-color: #ffa600; /* Active link background */
                    color: #ffffff; /* White text for active link */
                }
            </style>

            <div class="sidebar">
                <a href="/Avatars.html" class="no-hover">
                    <img id="profile-picture" src="character Background Removed.png" alt="Profile Picture">
                </a>
                <a href="/index.html" class="active">
                    <i class="fa fa-home"></i>
                    <h6><b><critter-translator-escaped key="home"></critter-translator-escaped></b></h6>
                </a>
                <a href="/profile.html">
                    <i class="fa fa-user"></i>
                    <h6><b><critter-translator-escaped key="profile"></critter-translator-escaped></b></h6>
                </a>
                <a href="/courses.html">
                    <i class="fa fa-book"></i>
                    <h6><b><critter-translator-escaped key="courses"></critter-translator-escaped></b></h6>
                </a>
                <a href="/EnrolledCourses.html">
                    <i class="fa fa-list-alt"></i>
                    <h6><b><critter-translator-escaped key="enrolled courses"></critter-translator-escaped></b></h6>
                </a>
                <a href="/MyPoints.html">
                    <i class="fa fa-star"></i>
                    <h6><b><critter-translator-escaped key="my points"></critter-translator-escaped></b></h6>
                </a>
                <a href="/DashBoard.html">
                    <i class="fa fa-star"></i>
                    <h6><b><critter-translator-escaped key="Dashboard"></critter-translator-escaped></b></h6>
                </a>
            </div>
        `;

        // Attach the created wrapper to the shadow DOM
        shadow.appendChild(wrapper);
    }
}

// Define the new element
customElements.define('sidebar-component', SidebarComponent);
