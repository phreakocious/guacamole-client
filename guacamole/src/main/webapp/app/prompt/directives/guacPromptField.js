/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * A directive for the guacamole client.
 */
angular.module('prompt').directive('guacPromptField', [function guacPromptField() {

    return {
        restrict: 'E',
        replace: true,
        scope: {

            /**
             * Any text to display before the field.
             *
             * @type String
             */
            pretext : '=',

            /**
             * The field to display.
             *
             * @type Field
             */
            field : '=',

            /**
             * The namespace of the field.
             *
             * @type String
             */
            namespace : '=',

            /**
             * The model for the field.
             *
             * @type String
             */
            model : '='

        },

        templateUrl: 'app/prompt/templates/guacPromptField.html',
        controller: ['$scope', '$injector', '$log', function guacPromptFieldController($scope,$injector,$log) {

            var translationStringService = $injector.get('translationStringService');

            $scope.responses = {};

            $scope.$watch('pretext', function(newtext) {
                $log.debug('>>>PROMPT<<< pretext: ' + newtext);
            });

        }]

    };
}]);
