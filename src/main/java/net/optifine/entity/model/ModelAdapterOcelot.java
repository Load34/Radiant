package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderOcelot;
import net.minecraft.entity.passive.EntityOcelot;
import net.optifine.reflect.Reflector;

import java.util.HashMap;
import java.util.Map;

public class ModelAdapterOcelot extends ModelAdapter {
	private static Map<String, Integer> mapPartFields = null;

	public ModelAdapterOcelot() {
		super(EntityOcelot.class, "ocelot", 0.4F);
	}

	private static Map<String, Integer> getMapPartFields() {
		if (mapPartFields == null) {
			mapPartFields = new HashMap<>();
			mapPartFields.put("back_left_leg", 0);
			mapPartFields.put("back_right_leg", 1);
			mapPartFields.put("front_left_leg", 2);
			mapPartFields.put("front_right_leg", 3);
			mapPartFields.put("tail", 4);
			mapPartFields.put("tail2", 5);
			mapPartFields.put("head", 6);
			mapPartFields.put("body", 7);
		}
		return mapPartFields;
	}

	public ModelBase makeModel() {
		return new ModelOcelot();
	}

	public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
		if (model instanceof ModelOcelot modelocelot) {
			Map<String, Integer> map = getMapPartFields();

			if (map.containsKey(modelPart)) {
				int i = map.get(modelPart);
				return (ModelRenderer) Reflector.getFieldValue(modelocelot, Reflector.ModelOcelot_ModelRenderers, i);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String[] getModelRendererNames() {
		return new String[]{"back_left_leg", "back_right_leg", "front_left_leg", "front_right_leg", "tail", "tail2", "head", "body"};
	}

	public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		return new RenderOcelot(rendermanager, modelBase, shadowSize);
	}
}
