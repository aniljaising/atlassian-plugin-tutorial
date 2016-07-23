//This is the main plugin servlet code for adminUI
package com.atlassian.plugins.tutorial.refapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import javax.inject.Inject;
import javax.inject.Named;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.collect.Maps;
import java.util.Map;

@Named("MyPluginServlet1")
public class MyPluginServlet extends HttpServlet
{
	private static final String PLUGIN_STORAGE_KEY = "com.atlassian.plugins.tutorial.refapp.adminui";
    private static final Logger log = LoggerFactory.getLogger(MyPluginServlet.class);
    
	@ComponentImport
    private final UserManager userManager;
	
	@ComponentImport
	private final LoginUriProvider loginUriProvider;
	
	@ComponentImport
	private final TemplateRenderer templateRenderer;
	
	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;
	
	@Inject
	public MyPluginServlet(UserManager userManager,
			LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer, PluginSettingsFactory pluginSettingsFactory) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.templateRenderer = templateRenderer;
		this.pluginSettingsFactory = pluginSettingsFactory;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException {
		
	PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
	pluginSettings.put(PLUGIN_STORAGE_KEY + ".name", req.getParameter("name"));
	pluginSettings.put(PLUGIN_STORAGE_KEY + ".age", req.getParameter("age"));
	response.sendRedirect("test");
	}
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username))
		{
			redirectToLogin(request, response);
			return;
		}
		Map<String, Object> context = Maps.newHashMap();

		PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

		if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".name") == null){
			String noName = "Enter a name here.";
			pluginSettings.put(PLUGIN_STORAGE_KEY +".name", noName);
		}

		if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".age") == null){
			String noAge = "Enter an age here.";
			pluginSettings.put(PLUGIN_STORAGE_KEY + ".age", noAge);
		}

		context.put("name", pluginSettings.get(PLUGIN_STORAGE_KEY + ".name"));
		context.put("age", pluginSettings.get(PLUGIN_STORAGE_KEY + ".age"));
		response.setContentType("text/html;charset=utf-8");
		
		
		templateRenderer.render("admin.vm", response.getWriter());
		
	}
	
	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}
	private URI getUri(HttpServletRequest request)
	{
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null)
		{
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	} 

}
