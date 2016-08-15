# atlassian-plugin-tutorial
Atlassian server plugin tutorial code built with JDK 8 and ideas on improving UI using JS libraries

1. The basics

a. Sign up for an account at http://developers.atlassian.com. Atlassian offers two plugin development SDKs for their cloud based and their in-house server based versions.

b. Consider signing up for an account at bitbucket.org if you don’t already have a SCM or if you want to try out a nice cloud based code collaboration tool that provides Git repos to host your code and also offers integration with several tool suites including a cloud based continuous integration and delivery tool. More on this later…

2. Issues with the atlassian tutorial

I started exploring the server side development and accessed the atlassian tutorial at https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project. The tutorial does a great job at the setup and gets you going, but it is dated.

Most of my time really went into trouble shooting issues with dependencies in pom.xml and conflicting directives in the plugin config file: atlassian-plugin.xml

Following a few sub links in the tutorial:

You are directed to convert the plugin component to a servlet model at https://developer.atlassian.com/docs/getting-started/learn-the-development-platform-by-example/convert-component-to-servlet-module by adding the following lines in the atlassian-plugin.xml

<component key="myPluginComponent" class="com.atlassian.plugins.tutorial.refapp.MyPluginComponent" public="true">
 <interface>com.atlassian.plugins.tutorial.refapp.MyPluginServlet</interface>
</component>
<servlet name="adminUI" class="com.atlassian.plugins.tutorial.refapp.MyPluginServlet" key="test">
 <url-pattern>/test</url-pattern>
</servlet>
Next there are SAL (Secure Access Layer) class components that need to be imported into the project. Look at https://developer.atlassian.com/docs/getting-started/learn-the-development-platform-by-example/control-access-with-sal which directs you to add the following lines to your atlassian-plugin.xml

<component-import key=”templateRenderer” interface=”com.atlassian.templaterenderer.TemplateRenderer” filter=””/>
<component-import key=”userManager” interface=”com.atlassian.sal.api.user.UserManager” filter=””/>
<component-import key=”loginUriProvider” interface=”com.atlassian.sal.api.auth.LoginUriProvider” filter=””/>
<component-import key=”pluginSettingsFactory” interface=”com.atlassian.sal.api.pluginsettings.PluginSettingsFactory” filter=””/>

Compiling this code promptly delivers an error messsage while using the latest version of Atlassian spring scanner and JDK 1.8.  The error message is that servlet directive and the component import tags are not compatible

After a lot of time troubleshooting, I hit upon the reason. The latest version of spring scanner supports annotations instead of tags. Take a look at the revamp suggested at https://bitbucket.org/atlassian/atlassian-spring-scanner

The Atlassian-plugin.xml file should not contain any of the <component-import> tags  and in your servlet code add annotations such as the below

@ComponentImport
    private final UserManager userManager;
	
	@ComponentImport
	private final LoginUriProvider loginUriProvider;
	
	@ComponentImport
	private final TemplateRenderer templateRenderer;
	
	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;
This needs to be updated in the tutorial.

2. Build for RefApp

Instead of developing for a particular tool like jira or confluence, developing for RefApp gives you the flexibility of completing your plugin development irrespective of the tool and then generating a plugin version for the target tool. RefApp provides the shared framework between all Atlassian applications. This means that you can develop your plugin without accidentally relying on dependencies or features specific to one application, or encountering an application-specific bug later on. Developing a plugin with RefApp eliminates guesswork about the functionality of your project. You can rest assured that since all Atlassian applications share at least the framework present in RefApp, your plugin will work as expected.





Some interesting ideas to explore once you have the basic tutorial up and running is how to take your plugin to the next level with ideas on how data should be retrieved only using the REST API and UI should be rendered on the client site by adding javascript libraries such as Babel and Webpack

Take a look at the details at the following two write ups….

https://developer.atlassian.com/blog/2016/06/jira-add-on-dev-2016-part-1/

https://developer.atlassian.com/blog/2016/06/jira-add-on-dev-2016-part-2/
