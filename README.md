# yif - Yaml Issues File
A YAML format for a list of issues.  Encode project issues
in a yaml file in the root of a project.  Edit the issues in
a text editor or specialized editor.

Each 'issues.yml' file consists of a list of Issue, where issue
is:

````
- !issue
id: <integer id of issue>
title: <text title of issue>
complete: <true or false>
priority: <integer, bigger is higher priority>
tags: <list of strings>
created: <date or null>
due: <date or null>
notes: <list of strings>
````

An example:

````
- !issue
id: 219
title: Learn more YAML 2nd
complete: true
priority: 10
tags: [learning, iiy]
created: 2012-01-17T13:01:33.355Z
due: null
notes: [Important to understand YAML notation, Note 2, Here is
my latest thoughts on the subject.]
- !issue
id: 854
title: Write eclipse editor for issues.yaml
complete: true
priority: 10
tags: [dev tools, iiy]
created: 2012-01-17T13:01:33.355Z
due: 2012-02-06T13:01:33.355Z
notes:
- write model
- write editor
- |
Note that the 'part' property still has a global tag but the
'wheel' property does not (because the wheel's runtime class
````

Current editors available:

- Eclipse
- Python (shell)