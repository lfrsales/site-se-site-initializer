# Liferay SE Site Initializer

A basic description of my project.

Developed to run on the following versions of Liferay: `Liferay DXP 7.3`

Built with [Liferay Workspace](https://help.liferay.com/hc/en-us/articles/360029147471-Liferay-Workspace) and [Blade CLI](https://help.liferay.com/hc/en-us/articles/360029147071-Blade-CLI).

## How to Build and Deploy to Liferay

Follow the steps below to build and deploy or copy the modules from the [releases](../../releases/latest) page to your Liferay's deploy folder.

In order to build or deploy this module you will need to [install Blade CLI](https://help.liferay.com/hc/en-us/articles/360028833852-Installing-Blade-CLI).

### To Build

`$ blade gw build`

You can find the built modules at `modules/{module-name}/build/libs/{module-name}.jar`.

### To Deploy

In `gradle-local.properties` add the following line to point towards the Liferay instance you want to deploy to:
```
liferay.workspace.home.dir=/path/to/liferay/home
```

`$ blade gw deploy`

## Usage

To use, create a new site and select the `SE Demo Site` template.

### Features

When deployed this module will create a new site template that will be auto populated with the content and resources from the dependencies folder of this project. The site initializer will import content and resources that follow the following format:

```
├── resources
│   ├── META-INF
│   │   └── resources
│   │       └── images
│   │           └── thumbnail.png
│   └── {full.package.path}
│        └── dependencies
│            ├── asset-list-entries
│            │   └── test-asset-list
│            │       └── asset-list-entry.json
│            ├── ddm-structures
│            │   └── destination-structure.xml
│            ├── ddm-templates
│            │   └── destination
│            │       ├── ddm_template.ftl
│            │       └── ddm_template.json
│            ├── display-page-templates
│            │   ├── default-display-page-templates.json
│            │   └── destination
│            │       ├── display-page-template.json
│            │       ├── page-definition.json
│            │       └── thumbnail.png
│            ├── journal-articles
│            │   └── journal-article-01
│            │       ├── journal_article.json
│            │       └── journal_article.xml
│            ├── journal-folders
│            │   └── journal_folders.json
│            ├── layouts
│            │   ├── destinations
│            │   │   ├── page-definition.json
│            │   │   └── page.json
│            │   ├── home
│            │   │   ├── page-definition.json
│            │   │   └── page.json
│            │   ├── layouts.json
│            │   └── news
│            │       ├── page-definition.json
│            │       └── page.json
│            ├── master-pages
│            │   └── test-master
│            │       ├── master-page.json
│            │       ├── page-definition.json
│            │       └── thumbnail.png
│            ├── site-navigation-menus
│            │   └── site-navigation-menus.json
│            ├── style-books
│            │   └── style-books.json
│            └── widget-templates
│                └── destination-publisher
│                    ├── ddm_template.ftl
│                    └── ddm_template.json
└── zippableResources
    ├── images
    │   └── test.png
    └── style-books
        └── darktheme
            ├── frontend-tokens-values.json
            ├── style-book.json
            └── thumbnail.png
```

## Issues & Questions Welcome