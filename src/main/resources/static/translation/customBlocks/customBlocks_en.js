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
Blockly.Blocks['properties'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("critter")
            .appendField(new Blockly.FieldDropdown([["terrain","TERRAIN"], ["size","SIZE"], ["shirtColor","BODYCOLOR"], ["x-coord","XCOORD"], ["y-coord","YCOORD"], ["canWalkOnWater","CANWALKONWATER"], ["haircolor","HATCOLOR"]]), "properties");
        this.setInputsInline(false);
        this.setOutput(true, null);
        this.setColour(330);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['properties_user'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("critter")
            .appendField(new Blockly.FieldDropdown([["size","SIZE"], ["shirtColor","BODYCOLOR"], ["haircolor","HATCOLOR"]]), "properties");
        this.setInputsInline(false);
        this.setOutput(true, null);
        this.setColour(330);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['set'] = {
    init: function() {
        this.appendValueInput("value1")
            .setCheck(null)
            .appendField("set");
        this.appendValueInput("value2")
            .setCheck(null)
            .appendField("to");
        this.setInputsInline(true);
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
        this.setColour(75);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['assert'] = {
    init: function() {
        this.appendValueInput("target")
            .setCheck(null)
            .appendField("assert");
        this.appendValueInput("value")
            .setCheck(null)
            .appendField("equals");
        this.setInputsInline(true);
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
        this.setColour(120);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['variable'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("variable")
            .appendField(new Blockly.FieldTextInput("name"), "NAME");
        this.setOutput(true, null);
        this.setColour(270);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.defineBlocksWithJsonArray([
    {
        "type": "dirt",
        "message0": "dirt",
        "output": null,
        "colour": 165,
        "tooltip": "",
        "helpUrl": "",
        "extensions": [
            "custom_text_quotes"
        ]
    },
    {
        "type": "grass",
        "message0": "grass",
        "output": null,
        "colour": 165,
        "tooltip": "",
        "helpUrl": "",
        "extensions": [
            "custom_text_quotes"
        ]
    },
    {
        "type": "water",
        "message0": "water",
        "output": null,
        "colour": 165,
        "tooltip": "",
        "helpUrl": "",
        "extensions": [
            "custom_text_quotes"
        ]
    },
    {
        "type": "lava",
        "message0": "lava",
        "output": null,
        "colour": 165,
        "tooltip": "",
        "helpUrl": "",
        "extensions": [
            "custom_text_quotes"
        ]
    },
    {
        "type": "ice",
        "message0": "ice",
        "output": null,
        "colour": 165,
        "tooltip": "",
        "helpUrl": "",
        "extensions": [
            "custom_text_quotes"
        ]
    }
]);

Blockly.Constants.Terrain = {};

Blockly.Constants.Terrain.CUSTOM_TEXT_QUOTES_EXTENSION = function() {
    this.mixin(Blockly.Constants.Terrain.QUOTE_IMAGE_MIXIN);
    this.quoteTerrain_();
};

Blockly.Constants.Terrain.QUOTE_IMAGE_MIXIN = {
    quoteTerrain_: function () {
        for (var i = 0, input; input = this.inputList[i]; i++) {
            input.insertFieldAt(0, this.newQuote_(true));
            input.insertFieldAt(2, this.newQuote_(false));
            return;
        }
        console.warn('field named "' + fieldName + '" not found in ' + this.toDevString());
    },

    newQuote_: function(open) {
        var isLeft = this.RTL? !open : open;
        var dataUri = isLeft ?
            this.QUOTE_IMAGE_LEFT_DATAURI :
            this.QUOTE_IMAGE_RIGHT_DATAURI;
        return new Blockly.FieldImage(
            dataUri,
            this.QUOTE_IMAGE_WIDTH,
            this.QUOTE_IMAGE_HEIGHT,
            isLeft ? '\u201C' : '\u201D');
    },

    QUOTE_IMAGE_LEFT_DATAURI:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAKCAQAAAAqJXdxAAAA' +
    'n0lEQVQI1z3OMa5BURSF4f/cQhAKjUQhuQmFNwGJEUi0RKN5rU7FHKhpjEH3TEMtkdBSCY' +
    '1EIv8r7nFX9e29V7EBAOvu7RPjwmWGH/VuF8CyN9/OAdvqIXYLvtRaNjx9mMTDyo+NjAN1' +
    'HNcl9ZQ5oQMM3dgDUqDo1l8DzvwmtZN7mnD+PkmLa+4mhrxVA9fRowBWmVBhFy5gYEjKMf' +
    'z9AylsaRRgGzvZAAAAAElFTkSuQmCC',

    QUOTE_IMAGE_RIGHT_DATAURI:
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAKCAQAAAAqJXdxAAAA' +
    'qUlEQVQI1z3KvUpCcRiA8ef9E4JNHhI0aFEacm1o0BsI0Slx8wa8gLauoDnoBhq7DcfWhg' +
    'gONDmJJgqCPA7neJ7p934EOOKOnM8Q7PDElo/4x4lFb2DmuUjcUzS3URnGib9qaPNbuXvB' +
    'O3sGPHJDRG6fGVdMSeWDP2q99FQdFrz26Gu5Tq7dFMzUvbXy8KXeAj57cOklgA+u1B5Aos' +
    'lLtGIHQMaCVnwDnADZIFIrXsoXrgAAAABJRU5ErkJggg==',

    QUOTE_IMAGE_WIDTH: 12,

    QUOTE_IMAGE_HEIGHT: 12,
};

Blockly.Blocks['cut_head'] = {
    init: function() {
        this.appendDummyInput()
            .setAlign(Blockly.ALIGN_RIGHT)
            .appendField("Code under Test");
        this.appendStatementInput("Content")
            .setCheck(null);
        this.setColour(120);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['init_head'] = {
    init: function() {
        this.appendDummyInput()
            .setAlign(Blockly.ALIGN_RIGHT)
            .appendField("Initialization");
        this.appendStatementInput("Content")
            .setCheck(null);
        this.setColour(300);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['shirt_picker'] = {
    init: function() {
        let options = [
            [{'src': 'images/cloth_red.png', 'width': 25, 'height': 25, 'alt': 'red'}, 'red'],
            [{'src': 'images/cloth_blue.png', 'width': 25, 'height': 25, 'alt': 'blue'}, 'blue'],
            [{'src': 'images/cloth_cyan.png', 'width': 25, 'height': 25, 'alt': 'cyan'}, 'cyan'],
            [{'src': 'images/cloth_green.png', 'width': 25, 'height': 25, 'alt': 'green'}, 'green'],
            [{'src': 'images/cloth_orange.png', 'width': 25, 'height': 25, 'alt': 'orange'}, 'orange'],
            [{'src': 'images/cloth_yellow.png', 'width': 25, 'height': 25, 'alt': 'yellow'}, 'yellow'],
            [{'src': 'images/cloth_white.png', 'width': 25, 'height': 25, 'alt': 'white'}, 'white'],
            [{'src': 'images/cloth_black.png', 'width': 25, 'height': 25, 'alt': 'black'}, 'black'],
            [{'src': 'images/cloth_pink.png', 'width': 25, 'height': 25, 'alt': 'pink'}, 'pink']
        ];
        if(window.matchMedia("(max-width: 600px)").matches) {
            options = [
                [{'src': 'images/cloth_red50.png', 'width': 15, 'height': 15, 'alt': 'red'}, 'red'],
                [{'src': 'images/cloth_blue50.png', 'width': 15, 'height': 15, 'alt': 'blue'}, 'blue'],
                [{'src': 'images/cloth_cyan50.png', 'width': 15, 'height': 15, 'alt': 'cyan'}, 'cyan'],
                [{'src': 'images/cloth_green50.png', 'width': 15, 'height': 15, 'alt': 'green'}, 'green'],
                [{'src': 'images/cloth_orange50.png', 'width': 15, 'height': 15, 'alt': 'orange'}, 'orange'],
                [{'src': 'images/cloth_yellow50.png', 'width': 15, 'height': 15, 'alt': 'yellow'}, 'yellow'],
                [{'src': 'images/cloth_white50.png', 'width': 15, 'height': 15, 'alt': 'white'}, 'white'],
                [{'src': 'images/cloth_black50.png', 'width': 15, 'height': 15, 'alt': 'black'}, 'black'],
                [{'src': 'images/cloth_pink50.png', 'width': 15, 'height': 15, 'alt': 'pink'}, 'pink']
            ];
        }
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown(options), "color");
        this.setOutput(true, null);
        this.setColour(30);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['hair_picker'] = {
    init: function() {
        let options = [
            [{'src': 'images/hair_blond.png', 'width': 25, 'height': 25, 'alt': 'blond'}, 'blond'],
            [{'src': 'images/hair_black.png', 'width': 25, 'height': 25, 'alt': 'black'}, 'black'],
            [{'src': 'images/hair_brown.png', 'width': 25, 'height': 25, 'alt': 'brown'}, 'brown'],
            [{'src': 'images/hair_gray.png', 'width': 25, 'height': 25, 'alt': 'gray'}, 'gray'],
            [{'src': 'images/hair_red.png', 'width': 25, 'height': 25, 'alt': 'red'}, 'red']
        ];
        if(window.matchMedia("(max-width: 600px)").matches) {
            options = [
                [{'src': 'images/hair_blond50.png', 'width': 15, 'height': 15, 'alt': 'blond'}, 'blond'],
                [{'src': 'images/hair_black50.png', 'width': 15, 'height': 15, 'alt': 'black'}, 'black'],
                [{'src': 'images/hair_brown50.png', 'width': 15, 'height': 15, 'alt': 'brown'}, 'brown'],
                [{'src': 'images/hair_gray50.png', 'width': 15, 'height': 15, 'alt': 'gray'}, 'gray'],
                [{'src': 'images/hair_red50.png', 'width': 15, 'height': 15, 'alt': 'red'}, 'red']
            ];
        }
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown(options), "color");
        this.setOutput(true, null);
        this.setColour(30);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};
