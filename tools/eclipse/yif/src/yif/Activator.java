package yif;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "yif"; //$NON-NLS-1$

	public static final String IMAGE_KEY_ACCEPT = "IMAGE_KEY_ACCEPT";

	public static final String IMAGE_KEY_BUG = "IMAGE_KEY_BUG";

	public static final String IMAGE_KEY_BULLET_BLUE = "IMAGE_KEY_BULLET_BLUE";

	public static final String IMAGE_KEY_CLOCK = "IMAGE_KEY_CLOCK";

	public static final String IMAGE_KEY_ASTRISK = "IMAGE_KEY_ASTRISK";

	public static final String IMAGE_KEY_DATE_NEXT = "IMAGE_KEY_DATE_NEXT";

	public static final String IMAGE_KEY_DATE_PREVIOUS = "IMAGE_KEY_DATE_PREVIOUS";

	public static final String IMAGE_KEY_PAGE = "IMAGE_KEY_PAGE";

	public static final String IMAGE_KEY_REPORT = "IMAGE_KEY_REPORT";

	public static final String IMAGE_KEY_TAG = "IMAGE_KEY_TAG";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ImageRegistry ir = getImageRegistry();
		ir.put(IMAGE_KEY_ACCEPT, getImageDescriptor("/icons/accept.png"));
		ir.put(IMAGE_KEY_BUG, getImageDescriptor("/icons/bug.png"));
		ir.put(IMAGE_KEY_BULLET_BLUE, getImageDescriptor("/icons/bullet_blue.png"));
		ir.put(IMAGE_KEY_CLOCK, getImageDescriptor("/icons/clock.png"));
		
		ir.put(IMAGE_KEY_ASTRISK, getImageDescriptor("/icons/asterisk_orange.png"));
		ir.put(IMAGE_KEY_DATE_NEXT, getImageDescriptor("/icons/date_next.png"));
		ir.put(IMAGE_KEY_DATE_PREVIOUS, getImageDescriptor("/icons/date_previous.png"));
		ir.put(IMAGE_KEY_PAGE, getImageDescriptor("/icons/page.png"));
		ir.put(IMAGE_KEY_REPORT, getImageDescriptor("/icons/report.png"));
		ir.put(IMAGE_KEY_TAG, getImageDescriptor("/icons/tag_blue.png"));
		
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
