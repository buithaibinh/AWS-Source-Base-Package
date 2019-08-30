package com.skg.aws_sample.amazon_aws;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.Subscription;
import com.apollographql.apollo.api.internal.Utils;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import type.CustomType;

@Generated("Apollo GraphQL")
public final class OnUpdateUserSubscription1 implements Subscription<OnUpdateUserSubscription1.Data, OnUpdateUserSubscription1.Data, Operation.Variables> {
    public static final String OPERATION_DEFINITION = "subscription OnUpdateUser {\n"
            + "  onUpdateUser {\n"
            + "    __typename\n"
            + "    Username\n"
            + "    Attributes {\n"
            + "      __typename\n"
            + "      Name\n"
            + "      Value\n"
            + "    }\n"
            + "    UserCreateDate\n"
            + "    UserLastModifiedDate\n"
            + "    Enabled\n"
            + "    UserStatus\n"
            + "  }\n"
            + "}";

    public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

    private static final OperationName OPERATION_NAME = new OperationName() {
        @Override
        public String name() {
            return "OnUpdateUser";
        }
    };

    private final Operation.Variables variables;

    public OnUpdateUserSubscription1() {
        this.variables = Operation.EMPTY_VARIABLES;
    }

    @Override
    public String operationId() {
        return "8f093ba841d7b3f23ed58dfbed1a794a65f037d9162c0ec60fcde1d7fa857457";
    }

    @Override
    public String queryDocument() {
        return QUERY_DOCUMENT;
    }

    @Override
    public OnUpdateUserSubscription1.Data wrapData(OnUpdateUserSubscription1.Data data) {
        return data;
    }

    @Override
    public Operation.Variables variables() {
        return variables;
    }

    @Override
    public ResponseFieldMapper<OnUpdateUserSubscription1.Data> responseFieldMapper() {
        return new Data.Mapper();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public OperationName name() {
        return OPERATION_NAME;
    }

    public static final class Builder {
        Builder() {
        }

        public OnUpdateUserSubscription1 build() {
            return new OnUpdateUserSubscription1();
        }
    }

    public static class Data implements Operation.Data {
        static final ResponseField[] $responseFields = {
                ResponseField.forObject("onUpdateUser", "onUpdateUser", null, true, Collections.<ResponseField.Condition>emptyList())
        };

        final @Nullable OnUpdateUser onUpdateUser;

        private volatile String $toString;

        private volatile int $hashCode;

        private volatile boolean $hashCodeMemoized;

        public Data(@Nullable OnUpdateUser onUpdateUser) {
            this.onUpdateUser = onUpdateUser;
        }

        public @Nullable OnUpdateUser onUpdateUser() {
            return this.onUpdateUser;
        }

        public ResponseFieldMarshaller marshaller() {
            return new ResponseFieldMarshaller() {
                @Override
                public void marshal(ResponseWriter writer) {
                    writer.writeObject($responseFields[0], onUpdateUser != null ? onUpdateUser.marshaller() : null);
                }
            };
        }

        @Override
        public String toString() {
            if ($toString == null) {
                $toString = "Data{"
                        + "onUpdateUser=" + onUpdateUser
                        + "}";
            }
            return $toString;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Data) {
                Data that = (Data) o;
                return ((this.onUpdateUser == null) ? (that.onUpdateUser == null) : this.onUpdateUser.equals(that.onUpdateUser));
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (!$hashCodeMemoized) {
                int h = 1;
                h *= 1000003;
                h ^= (onUpdateUser == null) ? 0 : onUpdateUser.hashCode();
                $hashCode = h;
                $hashCodeMemoized = true;
            }
            return $hashCode;
        }

        public static final class Mapper implements ResponseFieldMapper<Data> {
            final OnUpdateUser.Mapper onUpdateUserFieldMapper = new OnUpdateUser.Mapper();

            @Override
            public Data map(ResponseReader reader) {
                final OnUpdateUser onUpdateUser = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<OnUpdateUser>() {
                    @Override
                    public OnUpdateUser read(ResponseReader reader) {
                        return onUpdateUserFieldMapper.map(reader);
                    }
                });
                return new Data(onUpdateUser);
            }
        }
    }

    public static class OnUpdateUser {
        static final ResponseField[] $responseFields = {
                ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forCustomType("Username", "Username", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forList("Attributes", "Attributes", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("UserCreateDate", "UserCreateDate", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("UserLastModifiedDate", "UserLastModifiedDate", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forBoolean("Enabled", "Enabled", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("UserStatus", "UserStatus", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forList("groups", "groups", null, true, Collections.<ResponseField.Condition>emptyList())
        };

        final @Nonnull String __typename;

        final @Nonnull String Username;

        final @Nullable List<Attribute> Attributes;

        final @Nullable String UserCreateDate;

        final @Nullable String UserLastModifiedDate;

        final @Nullable Boolean Enabled;

        final @Nullable String UserStatus;

        final @Nullable List<Group> groups;

        private volatile String $toString;

        private volatile int $hashCode;

        private volatile boolean $hashCodeMemoized;

        public OnUpdateUser(@Nonnull String __typename, @Nonnull String Username,
                            @Nullable List<Attribute> Attributes, @Nullable String UserCreateDate,
                            @Nullable String UserLastModifiedDate, @Nullable Boolean Enabled,
                            @Nullable String UserStatus, @Nullable List<Group> groups) {
            this.__typename = Utils.checkNotNull(__typename, "__typename == null");
            this.Username = Utils.checkNotNull(Username, "Username == null");
            this.Attributes = Attributes;
            this.UserCreateDate = UserCreateDate;
            this.UserLastModifiedDate = UserLastModifiedDate;
            this.Enabled = Enabled;
            this.UserStatus = UserStatus;
            this.groups = groups;
        }

        public @Nonnull String __typename() {
            return this.__typename;
        }

        public @Nonnull String Username() {
            return this.Username;
        }

        public @Nullable List<Attribute> Attributes() {
            return this.Attributes;
        }

        public @Nullable String UserCreateDate() {
            return this.UserCreateDate;
        }

        public @Nullable String UserLastModifiedDate() {
            return this.UserLastModifiedDate;
        }

        public @Nullable Boolean Enabled() {
            return this.Enabled;
        }

        public @Nullable String UserStatus() {
            return this.UserStatus;
        }

        public @Nullable List<Group> groups() {
            return this.groups;
        }

        public ResponseFieldMarshaller marshaller() {
            return new ResponseFieldMarshaller() {
                @Override
                public void marshal(ResponseWriter writer) {
                    writer.writeString($responseFields[0], __typename);
                    writer.writeCustom((ResponseField.CustomTypeField) $responseFields[1], Username);
                    writer.writeList($responseFields[2], Attributes, new ResponseWriter.ListWriter() {
                        @Override
                        public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
                            listItemWriter.writeObject(((Attribute) value).marshaller());
                        }
                    });
                    writer.writeString($responseFields[3], UserCreateDate);
                    writer.writeString($responseFields[4], UserLastModifiedDate);
                    writer.writeBoolean($responseFields[5], Enabled);
                    writer.writeString($responseFields[6], UserStatus);
                    writer.writeList($responseFields[7], groups, new ResponseWriter.ListWriter() {
                        @Override
                        public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
                            listItemWriter.writeObject(((Group) value).marshaller());
                        }
                    });
                }
            };
        }

        @Override
        public String toString() {
            if ($toString == null) {
                $toString = "OnUpdateUser{"
                        + "__typename=" + __typename + ", "
                        + "Username=" + Username + ", "
                        + "Attributes=" + Attributes + ", "
                        + "UserCreateDate=" + UserCreateDate + ", "
                        + "UserLastModifiedDate=" + UserLastModifiedDate + ", "
                        + "Enabled=" + Enabled + ", "
                        + "UserStatus=" + UserStatus + ", "
                        + "groups=" + groups
                        + "}";
            }
            return $toString;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof OnUpdateUser) {
                OnUpdateUser that = (OnUpdateUser) o;
                return this.__typename.equals(that.__typename)
                        && this.Username.equals(that.Username)
                        && ((this.Attributes == null) ? (that.Attributes == null) : this.Attributes.equals(that.Attributes))
                        && ((this.UserCreateDate == null) ? (that.UserCreateDate == null) : this.UserCreateDate.equals(that.UserCreateDate))
                        && ((this.UserLastModifiedDate == null) ? (that.UserLastModifiedDate == null) : this.UserLastModifiedDate.equals(that.UserLastModifiedDate))
                        && ((this.Enabled == null) ? (that.Enabled == null) : this.Enabled.equals(that.Enabled))
                        && ((this.UserStatus == null) ? (that.UserStatus == null) : this.UserStatus.equals(that.UserStatus))
                        && ((this.groups == null) ? (that.groups == null) : this.groups.equals(that.groups));
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (!$hashCodeMemoized) {
                int h = 1;
                h *= 1000003;
                h ^= __typename.hashCode();
                h *= 1000003;
                h ^= Username.hashCode();
                h *= 1000003;
                h ^= (Attributes == null) ? 0 : Attributes.hashCode();
                h *= 1000003;
                h ^= (UserCreateDate == null) ? 0 : UserCreateDate.hashCode();
                h *= 1000003;
                h ^= (UserLastModifiedDate == null) ? 0 : UserLastModifiedDate.hashCode();
                h *= 1000003;
                h ^= (Enabled == null) ? 0 : Enabled.hashCode();
                h *= 1000003;
                h ^= (UserStatus == null) ? 0 : UserStatus.hashCode();
                h *= 1000003;
                h ^= (groups == null) ? 0 : groups.hashCode();
                $hashCode = h;
                $hashCodeMemoized = true;
            }
            return $hashCode;
        }

        public static final class Mapper implements ResponseFieldMapper<OnUpdateUser> {
            final Attribute.Mapper attributeFieldMapper = new Attribute.Mapper();

            final Group.Mapper groupFieldMapper = new Group.Mapper();

            @Override
            public OnUpdateUser map(ResponseReader reader) {
                final String __typename = reader.readString($responseFields[0]);
                final String Username = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[1]);
                final List<Attribute> Attributes = reader.readList($responseFields[2], new ResponseReader.ListReader<Attribute>() {
                    @Override
                    public Attribute read(ResponseReader.ListItemReader listItemReader) {
                        return listItemReader.readObject(new ResponseReader.ObjectReader<Attribute>() {
                            @Override
                            public Attribute read(ResponseReader reader) {
                                return attributeFieldMapper.map(reader);
                            }
                        });
                    }
                });
                final String UserCreateDate = reader.readString($responseFields[3]);
                final String UserLastModifiedDate = reader.readString($responseFields[4]);
                final Boolean Enabled = reader.readBoolean($responseFields[5]);
                final String UserStatus = reader.readString($responseFields[6]);
                final List<Group> groups = reader.readList($responseFields[7], new ResponseReader.ListReader<Group>() {
                    @Override
                    public Group read(ResponseReader.ListItemReader listItemReader) {
                        return listItemReader.readObject(new ResponseReader.ObjectReader<Group>() {
                            @Override
                            public Group read(ResponseReader reader) {
                                return groupFieldMapper.map(reader);
                            }
                        });
                    }
                });
                return new OnUpdateUser(__typename, Username, Attributes, UserCreateDate, UserLastModifiedDate, Enabled, UserStatus, groups);
            }
        }
    }

    public static class Attribute {
        static final ResponseField[] $responseFields = {
                ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("Name", "Name", null, false, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("Value", "Value", null, false, Collections.<ResponseField.Condition>emptyList())
        };

        final @Nonnull String __typename;

        final @Nonnull String Name;

        final @Nonnull String Value;

        private volatile String $toString;

        private volatile int $hashCode;

        private volatile boolean $hashCodeMemoized;

        public Attribute(@Nonnull String __typename, @Nonnull String Name, @Nonnull String Value) {
            this.__typename = Utils.checkNotNull(__typename, "__typename == null");
            this.Name = Utils.checkNotNull(Name, "Name == null");
            this.Value = Utils.checkNotNull(Value, "Value == null");
        }

        public @Nonnull String __typename() {
            return this.__typename;
        }

        public @Nonnull String Name() {
            return this.Name;
        }

        public @Nonnull String Value() {
            return this.Value;
        }

        public ResponseFieldMarshaller marshaller() {
            return new ResponseFieldMarshaller() {
                @Override
                public void marshal(ResponseWriter writer) {
                    writer.writeString($responseFields[0], __typename);
                    writer.writeString($responseFields[1], Name);
                    writer.writeString($responseFields[2], Value);
                }
            };
        }

        @Override
        public String toString() {
            if ($toString == null) {
                $toString = "Attribute{"
                        + "__typename=" + __typename + ", "
                        + "Name=" + Name + ", "
                        + "Value=" + Value
                        + "}";
            }
            return $toString;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Attribute) {
                Attribute that = (Attribute) o;
                return this.__typename.equals(that.__typename)
                        && this.Name.equals(that.Name)
                        && this.Value.equals(that.Value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (!$hashCodeMemoized) {
                int h = 1;
                h *= 1000003;
                h ^= __typename.hashCode();
                h *= 1000003;
                h ^= Name.hashCode();
                h *= 1000003;
                h ^= Value.hashCode();
                $hashCode = h;
                $hashCodeMemoized = true;
            }
            return $hashCode;
        }

        public static final class Mapper implements ResponseFieldMapper<Attribute> {
            @Override
            public Attribute map(ResponseReader reader) {
                final String __typename = reader.readString($responseFields[0]);
                final String Name = reader.readString($responseFields[1]);
                final String Value = reader.readString($responseFields[2]);
                return new Attribute(__typename, Name, Value);
            }
        }
    }

    public static class Group {
        static final ResponseField[] $responseFields = {
                ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("GroupName", "GroupName", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("UserPoolId", "UserPoolId", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("Description", "Description", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forInt("Precedence", "Precedence", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("CreationDate", "CreationDate", null, true, Collections.<ResponseField.Condition>emptyList()),
                ResponseField.forString("LastModifiedDate", "LastModifiedDate", null, true, Collections.<ResponseField.Condition>emptyList())
        };

        final @Nonnull String __typename;

        final @Nullable String GroupName;

        final @Nullable String UserPoolId;

        final @Nullable String Description;

        final @Nullable Integer Precedence;

        final @Nullable String CreationDate;

        final @Nullable String LastModifiedDate;

        private volatile String $toString;

        private volatile int $hashCode;

        private volatile boolean $hashCodeMemoized;

        public Group(@Nonnull String __typename, @Nullable String GroupName,
                     @Nullable String UserPoolId, @Nullable String Description, @Nullable Integer Precedence,
                     @Nullable String CreationDate, @Nullable String LastModifiedDate) {
            this.__typename = Utils.checkNotNull(__typename, "__typename == null");
            this.GroupName = GroupName;
            this.UserPoolId = UserPoolId;
            this.Description = Description;
            this.Precedence = Precedence;
            this.CreationDate = CreationDate;
            this.LastModifiedDate = LastModifiedDate;
        }

        public @Nonnull String __typename() {
            return this.__typename;
        }

        public @Nullable String GroupName() {
            return this.GroupName;
        }

        public @Nullable String UserPoolId() {
            return this.UserPoolId;
        }

        public @Nullable String Description() {
            return this.Description;
        }

        public @Nullable Integer Precedence() {
            return this.Precedence;
        }

        public @Nullable String CreationDate() {
            return this.CreationDate;
        }

        public @Nullable String LastModifiedDate() {
            return this.LastModifiedDate;
        }

        public ResponseFieldMarshaller marshaller() {
            return new ResponseFieldMarshaller() {
                @Override
                public void marshal(ResponseWriter writer) {
                    writer.writeString($responseFields[0], __typename);
                    writer.writeString($responseFields[1], GroupName);
                    writer.writeString($responseFields[2], UserPoolId);
                    writer.writeString($responseFields[3], Description);
                    writer.writeInt($responseFields[4], Precedence);
                    writer.writeString($responseFields[5], CreationDate);
                    writer.writeString($responseFields[6], LastModifiedDate);
                }
            };
        }

        @Override
        public String toString() {
            if ($toString == null) {
                $toString = "Group{"
                        + "__typename=" + __typename + ", "
                        + "GroupName=" + GroupName + ", "
                        + "UserPoolId=" + UserPoolId + ", "
                        + "Description=" + Description + ", "
                        + "Precedence=" + Precedence + ", "
                        + "CreationDate=" + CreationDate + ", "
                        + "LastModifiedDate=" + LastModifiedDate
                        + "}";
            }
            return $toString;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Group) {
                Group that = (Group) o;
                return this.__typename.equals(that.__typename)
                        && ((this.GroupName == null) ? (that.GroupName == null) : this.GroupName.equals(that.GroupName))
                        && ((this.UserPoolId == null) ? (that.UserPoolId == null) : this.UserPoolId.equals(that.UserPoolId))
                        && ((this.Description == null) ? (that.Description == null) : this.Description.equals(that.Description))
                        && ((this.Precedence == null) ? (that.Precedence == null) : this.Precedence.equals(that.Precedence))
                        && ((this.CreationDate == null) ? (that.CreationDate == null) : this.CreationDate.equals(that.CreationDate))
                        && ((this.LastModifiedDate == null) ? (that.LastModifiedDate == null) : this.LastModifiedDate.equals(that.LastModifiedDate));
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (!$hashCodeMemoized) {
                int h = 1;
                h *= 1000003;
                h ^= __typename.hashCode();
                h *= 1000003;
                h ^= (GroupName == null) ? 0 : GroupName.hashCode();
                h *= 1000003;
                h ^= (UserPoolId == null) ? 0 : UserPoolId.hashCode();
                h *= 1000003;
                h ^= (Description == null) ? 0 : Description.hashCode();
                h *= 1000003;
                h ^= (Precedence == null) ? 0 : Precedence.hashCode();
                h *= 1000003;
                h ^= (CreationDate == null) ? 0 : CreationDate.hashCode();
                h *= 1000003;
                h ^= (LastModifiedDate == null) ? 0 : LastModifiedDate.hashCode();
                $hashCode = h;
                $hashCodeMemoized = true;
            }
            return $hashCode;
        }

        public static final class Mapper implements ResponseFieldMapper<Group> {
            @Override
            public Group map(ResponseReader reader) {
                final String __typename = reader.readString($responseFields[0]);
                final String GroupName = reader.readString($responseFields[1]);
                final String UserPoolId = reader.readString($responseFields[2]);
                final String Description = reader.readString($responseFields[3]);
                final Integer Precedence = reader.readInt($responseFields[4]);
                final String CreationDate = reader.readString($responseFields[5]);
                final String LastModifiedDate = reader.readString($responseFields[6]);
                return new Group(__typename, GroupName, UserPoolId, Description, Precedence, CreationDate, LastModifiedDate);
            }
        }
    }
}
