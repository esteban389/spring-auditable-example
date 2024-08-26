# Introduction
This project is a basic and probably oversimplified example on how i would do auditing in spring boot using hibernate.

# How it works
All entities must extend a custom base entity named AuditableEntity which contains some extra (and one could argue unnecesary) columns / attributes, what i really care about this is the soft delete capabilities for all auditable entites by having a common status field


The real core is in the HibernateEventListeners and HibernateEventListenersConfigurer which creates and registers event listeners to the post-insert and post-update entity lifecycle events, and creates a new record to the auditable table, in the update listener if the status changes it will mark the operation as either delete or restore based on the new value
