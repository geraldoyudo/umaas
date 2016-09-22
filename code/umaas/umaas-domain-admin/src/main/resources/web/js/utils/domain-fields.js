/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module("app")

    .constant("userFields", [ {              
                    key: 'username',
                    type: 'input',
                    hideExpression: 'false',
                    templateOptions: {
                        type: 'text',
                        label: 'Username',
                        placeholder: 'Enter the Username',
                        required: true
                    }
                },
                {
                    key: 'password',
                    type: 'input',
                    templateOptions: {
                        type: 'password',
                        label: 'Password',
                        placeholder: 'Enter your password',
                        required: true
                    }
                },
                {
                    key: 'email',
                    type: 'input',
                    templateOptions: {
                        type: 'email',
                        label: 'Email',
                        placeholder: 'Enter your email',
                        required: true
                    }
                },
                {
                    key: 'emailVerified',
                    type: 'checkbox',
                    templateOptions: {
                        label: 'Email Verified'
                       
                    }
                },
                 {
                    key: 'phoneNumberVerified',
                    type: 'checkbox',
                    templateOptions: {
                        label: 'Phone Number Verified'
                       
                    }
                },
                 {
                    key: 'groups',
                    type: 'arrayInput',
                    templateOptions: {
                        label: 'Groups',
                        options: [{name: "opt1", id:1},{name:"opt2", id:2}]
                    }
                }])
            
    .constant("customFieldFields", [{
                    key: 'name',
                    type: 'input',
                    templateOptions: {
                        type: 'text',
                        label: 'Name',
                        placeholder: 'Enter field name',
                        required: true
                    }
                },
                {
                    key: 'type',
                    type: 'select',
                    templateOptions: {
                        label: 'Type',
                        options: [ {name: "Boolean", value: "boolean"}, {name: "Integer", value:"integer"}, 
                            {name: "String", value:"string"}, {name: "Decimal", value:"decimal"}, 
                            {name: "Date", value:"date"},{name: "Email", value:"email"},{name: "Select", value:"select"},{name: "File", value:"file"}],      
                        required: true
                    }
                },
                {
                    key: 'mandatory',
                    type: 'checkbox',
                    templateOptions: {
                        label: 'Required'
                       
                    }
                },
                 {
                    key: 'registrationItem',
                    type: 'checkbox',
                    templateOptions: {
                        label: 'Include in Registration'
                    }
                },
                 {
                    key: 'properties.type',
                    type: 'select',
                    hideExpression: "model.type !== 'select'",
                    templateOptions: {
                        label: 'Option Type',
                         options: [ {name: "Boolean", value: "boolean"}, {name: "Integer", value:"integer"}, 
                            {name: "String", value:"string"}, {name: "Decimal", value:"decimal"}, 
                            {name: "Date", value:"date"}]         
                    }
                },
                {
                    
                    model: 'model.properties',
                    key: 'maximum',
                    type: 'input',
                    hideExpression: "!(model.type == 'integer' || model.type =='decimal')",
                    templateOptions: {
                        label: 'Maximum',
                        placeholder: 'Enter Maximum Value',
                        type: 'number'
                    }
                },
                {
                    
                    model: 'model.properties',
                    key: 'minimum',
                    type: 'input',
                    hideExpression: "!(model.type == 'integer' || model.type =='decimal')",
                    templateOptions: {
                        label: 'Minimum',
                        placeholder: 'Enter Minimum Value',
                        type: 'number'
                    }
                },
                {
                    
                    model: 'model.properties',
                    key: 'maxDate',
                    type: 'datepicker',
                    hideExpression: "model.type !== 'date'",
                    templateOptions: {
                        label: 'Maximum',
                        placeholder: 'Enter Maximum Date'
                    }
                },
                {
                    
                    model: 'model.properties',
                    key: 'minDate',
                    type: 'datepicker',
                    hideExpression: "model.type !== 'date'",
                    templateOptions: {
                        label: 'Minimum',
                        placeholder: 'Enter Minimum Date'
                    }
                },
                 {
                    
                    model: 'model.properties',
                    key: 'options',
                    type: 'chips',
                    hideExpression: "model.type !== 'select'",
                    templateOptions: {
                        label: 'Select Options',
                        placeholder: 'Add option',
                        secondaryPlaceholder: '+option',
                        optionsKey: 'options',
                        key: "properties.options"
                    }
                },
                {
                    
                    model: 'model.properties',
                    key: 'useLabel',
                    type: 'checkbox',
                    hideExpression: "model.type !== 'select'",
                    templateOptions: {
                        label: 'Use Label?'
                    }
                },
                {
                    
                    model: 'model.properties',
                    key: 'labels',
                    type: 'chips',
                    hideExpression: "model.type !== 'select' && !model.properties.useLabel",
                    templateOptions: {
                        label: 'Select Labels',
                        placeholder: 'Add label',
                        secondaryPlaceholder: '+label',
                        optionsKey: 'labels',
                        key: "properties.labels"
                    }
                },
                {
                    key: 'properties.pattern',
                    type: 'input',
                    hideExpression: "model.type !== 'string'",
                    templateOptions: {
                        type: 'text',
                        label: 'Pattern',
                        placeholder: 'Enter allowed pattern',
                        required: false
                    }
                },
                 {
                    key: 'properties.title',
                    type: 'input',
                    hideExpression: "model.type !== 'string' ||  !model.properties.pattern",
                    templateOptions: {
                        type: 'text',
                        label: 'Pattern Mismatch Message',
                        placeholder: 'Enter message to display on mismatch',
                        required: true
                    }
                },
                {
                    key: 'properties.accept',
                    type: 'input',
                    hideExpression: "model.type !== 'file'",
                    templateOptions: {
                        type: 'text',
                        label: 'HTML5 Accept type (jpg, docx, video/*, application/json e.t.c)',
                        placeholder: 'Enter preferred extension',
                        required: true,
                        pattern: '\\w+([\/]{1}(([*]{0,1})|(\\w+)){1}){0,1}'
                    }
                },
                {
                    key: 'properties.extension',
                    type: 'input',
                    hideExpression: "model.type !== 'file'",
                    templateOptions: {
                        type: 'text',
                        label: 'File extension type (jpg, docx, png, pdf)',
                        placeholder: 'Enter preferred extension',
                        required: true,
                        pattern: "\\w+([.][\\w])*"
                    }
                },
                 {
                    key: 'properties.limit',
                    type: 'input',
                    hideExpression: "model.type !== 'file'",
                    templateOptions: {
                        type: 'number',
                        label: 'File size limit (in bytes)',
                        placeholder: 'Enter size limit',
                        required: true
                    }
                }
                ])
                
    .constant("roleMappingFields",[
                {
                    key: 'type',
                    type: 'select',
                    templateOptions: {
                        label: 'Type',
                        options: [ {name: "Group", value: "Group"}, {name: "User", value:"User"}]      
                    }
                },
                {
                    key: 'key',
                    type: 'httpOptionsSelect',
                    hideExpression: "model.type !== 'User'",
                    templateOptions: {
                        label: 'Username',
                        url: '/api/appUsers/search/findAllInDomainContainingUsername',
                        queryKey: 'username',
                        itemKey: 'username',
                        resourceName: 'appUsers',
                        placeholder: 'Enter Username',
                        required: true,
                        params:{
                            domain: ""
                        }
                        // Call our province service to get a list
                        // of provinces and territories
                       
                    }
                },
                {
                    key: 'key',
                    type: 'httpOptionsSelect',
                    hideExpression: "model.type !== 'Group'",
                    templateOptions: {
                        label: 'Group Name',
                        url: '/api/groups/search/findAllByDomain',
                        itemKey: 'name',
                        resourceName: 'groups',
                        placeholder: 'Enter Group',
                        required: true,
                        params:{
                            domain: ""
                        }
                        // Call our province service to get a list
                        // of provinces and territories
                       
                    }
                },
                
                {
                    key: 'roles',
                    type: 'arrayInput',
                    templateOptions: {
                        label: 'Roles'
                         
                    }
                }
            ])
    .constant("roleFields", [
                {
                    key: 'name',
                    type: 'input',
                    templateOptions: {
                        type: 'text',
                        label: 'Name',
                        placeholder: 'Enter Role Name',
                        required: true
                    }
                }
                ])
                
    .constant("groupFields", [
                {
                    key: 'name',
                    type: 'input',
                    templateOptions: {
                        type: 'text',
                        label: 'Name',
                        placeholder: 'Enter Group Name',
                        required: true
                    }
                }
   ])
   
    .constant("permissionFields", [
                {
                    key: 'type',
                    type: 'select',
                    hideExpression: "true",
                    templateOptions: {
                        label: 'Type',
                        options: [ {name: "Group", value: "Group"}, {name: "User", value:"User"}]      
                    }
                    
                },
                {
                    key: 'key',
                    type: 'input',
                    hideExpression: "model.type !== 'User'",
                    templateOptions: {
                        label: 'Email',
                        placeholder: 'Enter User Email',
                        required: true
                       
                    }
                },
                {
                    key: 'key',
                    type: 'httpOptionsSelect',
                    hideExpression: "model.type !== 'Group'",
                    templateOptions: {
                        label: 'Group Name',
                        url: '/api/groups/search/findAllByDomain',
                        itemKey: 'name',
                        resourceName: 'groups',
                        placeholder: 'Enter Group Name',
                        required: true,
                        params:{
                            domain: '__admin__'
                        }
                        // Call our province service to get a list
                        // of provinces and territories
                       
                    }
                },
                {
                    key: 'actions',
                    type: 'arrayInput',
                    templateOptions: {
                        label: 'Actions',
                        options:[{id: 1, name: "admin"}]
                    }
                }
   ])
           .constant("domainFields",[
        {
             key: 'id',
             type: 'input',
            
             templateOptions: {
                 type: 'text',
                 label: 'Domain Id',
                 required: true,
                  disabled: true
             }
         },
         {
             key: 'appName',
             type: 'input',
             templateOptions: {
                 type: 'text',
                 label: 'Domain Name',
                 placeholder: 'Please Enter a Domain Name',
                 required: true
             }
         },
         {
             key: 'code',
             type: 'input',
                         
             templateOptions: {
                 type: 'text',
                 label: 'Domain Code',
                 required: true,
                  disabled: true
             }
         },
         {

             key: 'urlMap.home',
             type: 'input',
             templateOptions: {
                 type: 'text',
                 label: 'Url to home page',
                 placeholder: 'Enter home page url'
             }
         },
         {

             key: 'urlMap.login',
             type: 'input',
             templateOptions: {
                 type: 'text',
                 label: 'Url to Redirect on Success',
                 placeholder: 'Enter registration success url'
             }
         },
         {

             key: 'urlMap.registration_success',
             type: 'input',
             templateOptions: {
                 type: 'text',
                 label: 'Url to Redirect on Success',
                 placeholder: 'Enter registration success url'
             }
         },
         {

             key: 'urlMap.verification_success',
             type: 'input',
             templateOptions: {
                 type: 'text',
                 label: 'Url to Redirect on Email Verification Success',
                 placeholder: 'Enter email verification success url'
             }
         },
         {

             key: 'properties.emailAsUsername',
             type: 'checkbox',
             templateOptions: {
                label: 'Use Email as Username'

             }
         }
]);

