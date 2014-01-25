package com.congressionalphotodirectory.main;

import java.awt.image.BufferedImage;

import com.mortennobel.imagescaling.ResampleOp;

public class ImageEditor {

	/**
	 * Generate a thumbnail-sized image from the given original image.
	 * 
	 * @param original		The original, unaltered image
	 * @return				A proportionally resized image, appropriate for thumbnail use
	 */
	public static BufferedImage generateThumbnail( BufferedImage original ) {
		return ImageEditor.resizeImage(original, 150);
	}
	
	/**
	 * Generate an image appropriate for display on an iPhone, with a fixed width of 640 pixels.
	 * 
	 * @param original		The original, unaltered image
	 * @return				A proportionally resized image, appropriate for iPhone display
	 */
	public static BufferedImage generateIPhoneImage( BufferedImage original ) {
		return ImageEditor.resizeImage(original, 640);
	}
	
	/**
	 * Resize the given image, with a fixed given width, and proportionally calculated height
	 * 
	 * @param original		The original, unaltered image
	 * @param width			The fixed width of the new image
	 * @return				A proportionally resized image with fixed width and calculated height
	 */
	public static BufferedImage resizeImage( BufferedImage original, int width ) {
		
		int height = (width * original.getHeight()) / original.getWidth();
		
		BufferedImage resizedImage = null;
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask.VerySharp);
		
		resizedImage = resampleOp.filter(original, null);
		
		return resizedImage;
	}
	
}
