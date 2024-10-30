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
//goog.addDependency("../" + "/hair_field_colour.js", ['Blockly.HairFieldColour'], ['Blockly.Field', 'Blockly.utils', 'goog.dom', 'goog.events', 'goog.style', 'goog.ui.ColorPicker']);
//goog.require('Blockly.HairFieldColour');

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

Blockly.JavaScript['properties'] = function(block) {
    var dropdown_properties = block.getFieldValue('properties');
    var code;
    switch (dropdown_properties) {
        case 'TERRAIN' :
            code = 'this._globalData.level[this.position.y][this.position.x]';
            break;
        case 'SIZE':
            code = 'this.size';
            break;
        case 'BODYCOLOR':
            code = 'this.color';
            break;
        case 'XCOORD':
            code = 'this.position.x + 1';
            break;
        case 'YCOORD':
            code = 'this._globalData.height - this.position.y';
            break;
        case 'CANWALKONWATER':
            code = 'this.canWalkOnWater';
            break;
        case 'HATCOLOR':
            code = 'this.hair';
            break;
        default:
            code = "console.log('Cannot translate blockly to javaScript!')"
    }
    return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['properties_user'] = function(block) {
    var dropdown_properties = block.getFieldValue('properties');
    var code;
    switch (dropdown_properties) {
        case 'SIZE':
            code = 'this.size';
            break;
        case 'BODYCOLOR':
            code = 'this.color';
            break;
        case 'HATCOLOR':
            code = 'this.hair';
            break;
        default:
            code = "console.log('Cannot translate blockly to javaScript!')"
    }
    return [code, Blockly.JavaScript.ORDER_NONE];
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

Blockly.JavaScript['set'] = function(block) {
    var value_name1 = Blockly.JavaScript.valueToCode(block, 'value1', Blockly.JavaScript.ORDER_ATOMIC);
    var value_name2 = Blockly.JavaScript.valueToCode(block, 'value2', Blockly.JavaScript.ORDER_ATOMIC);
    var code = value_name1 + " = " + value_name2 + ";\n";
    return code;
};

Blockly.defineBlocksWithJsonArray([  // BEGIN JSON EXTRACT
    // Block for colour picker.
    {
        "type": "custom_colour_picker",
        "message0": "%1",
        "args0": [
            {
                "type": "field_colour",
                "name": "COLOUR",
                "colour": "#f0e527"
            }
        ],
        "output": "Colour",
        "colour": "%{BKY_COLOUR_HUE}",
        "helpUrl": "%{BKY_COLOUR_PICKER_HELPURL}",
        "tooltip": "%{BKY_COLOUR_PICKER_TOOLTIP}",
        "extensions": ["parent_tooltip_when_inline", "hair_colour_extension"]
    }
]);

Blockly.FieldColour.COLOURS = ['#FF0000','#0000FF','#00FFFF','#009900','#FFA500','#FFFF00','#FFFFFF','#000000','#FF00FF'];
Blockly.FieldColour.COLUMNS = 3;

Blockly.JavaScript['custom_colour_picker'] = function(block) {
    var colour_name = block.getFieldValue('COLOUR');
    var code;
    switch (colour_name.toLowerCase()) {
        case '#f0e527' :
            code = '"blond"';
            break;
        case '#1e1a22':
            code = '"black"';
            break;
        case '#552e0b':
            code = '"brown"';
            break;
        case '#7b7b7b':
            code = '"gray"';
            break;
        case '#b51d00':
            code = '"red"';
            break;
        default:
            code = "console.log('Cannot translate blockly to javaScript! Value: " + colour_name + ")";
    }
    return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['colour_picker'] = function(block) {
    var colour_name = block.getFieldValue('COLOUR');
    var code;
    switch (colour_name.toLowerCase()) {
        case '#ff0000' :
            code = '"red"';
            break;
        case '#0000ff':
            code = '"blue"';
            break;
        case '#00ffff':
            code = '"cyan"';
            break;
        case '#009900':
            code = '"green"';
            break;
        case '#ffa500':
            code = '"orange"';
            break;
        case '#ffff00':
            code = '"yellow"';
            break;
        case '#ffffff':
            code = '"white"';
            break;
        case '#000000':
            code = '"black"';
            break;
        case '#ff00ff':
            code = '"pink"';
            break;
        default:
            code = "console.log('Cannot translate blockly to javaScript! Value: " + colour_name + ")";
    }
    return [code, Blockly.JavaScript.ORDER_NONE];
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

Blockly.JavaScript['assert'] = function(block) {
    var value_target = Blockly.JavaScript.valueToCode(block, 'target', Blockly.JavaScript.ORDER_ATOMIC);
    var value_value = Blockly.JavaScript.valueToCode(block, 'value', Blockly.JavaScript.ORDER_ATOMIC);

    var code = 'if (' + value_target + ' !== ' + value_value + ') {\n' +
        '\tthis._killCritter(x, y); \n' +
        '}\n';
    return code;
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

Blockly.JavaScript['variable'] = function(block) {
    var text_name = block.getFieldValue('NAME');
    var code = 'this.variable_' + text_name;
    return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['dirt'] = function(block) {
    var code = '"dirt"';
    return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['grass'] = function(block) {
    var code = '"grass"';
    return [code, Blockly.JavaScript.ORDER_NONE];
};
Blockly.JavaScript['water'] = function(block) {
    var code = '"water"';
    return [code, Blockly.JavaScript.ORDER_NONE];
};
Blockly.JavaScript['lava'] = function(block) {
    var code = '"lava"';
    return [code, Blockly.JavaScript.ORDER_NONE];
};
Blockly.JavaScript['ice'] = function(block) {
    var code = '"ice"';
    return [code, Blockly.JavaScript.ORDER_NONE];
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

Blockly.Extensions.register('custom_text_quotes',
    Blockly.Constants.Terrain.CUSTOM_TEXT_QUOTES_EXTENSION);

Blockly.HairFieldColour = {};

Blockly.HairFieldColour.COLOURS = ['#f0e527','#1e1a22','#552e0b', '#7b7b7b','#b51d00'];
Blockly.HairFieldColour.COLUMNS = 3;

Blockly.HairFieldColour.HAIR_COLOUR_MIXIN = {
    changeColor_: function () {
        this.inputList[0].fieldRow[0] = cloneObject(this.inputList[0].fieldRow[0]);
        this.inputList[0].fieldRow[0].setColours(Blockly.HairFieldColour.COLOURS);
        this.inputList[0].fieldRow[0].setColumns(Blockly.HairFieldColour.COLUMNS);
    }
};
Blockly.HairFieldColour.HAIR_COLOUR_EXTENSION = function() {
    this.mixin(Blockly.HairFieldColour.HAIR_COLOUR_MIXIN);
    this.changeColor_();
};

Blockly.Extensions.register('hair_colour_extension',
    Blockly.HairFieldColour.HAIR_COLOUR_EXTENSION);

function cloneObject(obj) {


    var clone = {};
    for(var i in obj) {
        clone[i] = obj[i];
    }
    return clone;
}

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

Blockly.JavaScript['cut_head'] = function(block) {
    let statements_content = Blockly.JavaScript.statementToCode(block, 'Content');
    let code = '//CUT_START\n' + statements_content + "\n//CUT_END\n";
    return code;
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

Blockly.JavaScript['shirt_picker'] = function(block) {
    let dropdown_color = block.getFieldValue('color');
    let code = '"' + dropdown_color + '"';
    return [code, Blockly.JavaScript.ORDER_NONE];
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

Blockly.JavaScript['hair_picker'] = function(block) {
    let dropdown_color = block.getFieldValue('color');
    let code = '"' + dropdown_color + '"';
    return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['init_head'] = function(block) {
    let statements_content = Blockly.JavaScript.statementToCode(block, 'Content');
    let code = '//INIT_START\n' + statements_content + "\n//INIT_END\n";
    return code;
};
