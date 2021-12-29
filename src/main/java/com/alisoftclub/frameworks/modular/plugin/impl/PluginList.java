/*    */ package com.alisoftclub.frameworks.modular.plugin.impl;
/*    */ 
/*    */ import com.alisoftclub.frameworks.modular.plugin.Plugin;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PluginList
/*    */   extends ArrayList<Plugin>
/*    */ {
/*    */   public PluginList(int initialCapacity) {
/* 14 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   
/*    */   public PluginList() {}
/*    */   
/*    */   public PluginList(Collection<? extends Plugin> c) {
/* 21 */     super(c);
/*    */   }
/*    */ }


/* Location:              E:\java\reverse-engineered\reconciler\lib\modular-framework-1.0.0.1.jar!\com\alisoftclub\amf\plugin\impl\PluginList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */